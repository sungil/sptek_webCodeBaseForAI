package com._sptek._webFrameworkExample.unit.authentication.userStore;

import com._sptek.__webFramework.security.authentication.principal.FrameworkUserDetails;
import com._sptek._webFrameworkExample.unit.authentication.userStore.entity.User;
import com._sptek._webFrameworkExample.unit.authentication.userStore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 예제 업무 사용자 entity를 프레임워크 공통 인증 principal로 변환하는 UserDetailsService.
 *
 * <p>프레임워크는 사용자 저장소 구조를 알지 않고, 업무 영역이 조회한 사용자 정보를
 * {@link FrameworkUserDetails} 계약에 맞춰 반환한다.</p>
 *
 * <p>Spring Security의 form login은 AuthenticationProvider를 통해 이 서비스를 호출한다.
 * 이 클래스는 DB 조회와 업무 User -> FrameworkUserDetails 변환까지만 담당하고,
 * password 일치 여부 판단은 AuthenticationProvider가 수행한다.</p>
 */
@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * email 기준으로 사용자를 조회해 Spring Security가 사용할 공통 principal을 반환한다.
     *
     * <p>role name과 authority enum name을 모두 GrantedAuthority 문자열로 내려준다.
     * Spring Security의 hasRole/hasAuthority 검사는 결국 이 문자열 목록을 기준으로 동작한다.</p>
     */
    @Override
    public FrameworkUserDetails loadUserByUsername(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User(%s) not found.", userEmail)));

        Set<String> authorityNames = user.getRoles().stream()
                .flatMap(role -> {
                    Set<String> names = new LinkedHashSet<>();
                    names.add(role.getRoleName());
                    Optional.ofNullable(role.getAuthorities()).orElseGet(java.util.List::of).stream()
                            .map(authority -> authority.getAuthority().name())
                            .forEach(names::add);
                    return names.stream();
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        FrameworkUserDetails frameworkUserDetails = FrameworkUserDetails.builder()
                .userId(String.valueOf(user.getId()))
                .username(user.getEmail())
                .displayName(user.getName())
                .password(user.getPassword())
                .authorityNames(authorityNames)
                .build();

        log.debug("user info from userDetailsService by userName : {}, {}", frameworkUserDetails.getUsername(),
                frameworkUserDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return frameworkUserDetails;
    }
}
