package com._sptek._webFrameworkExample.unit.authentication.security;

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
 * 업무 사용자 저장소를 Spring Security UserDetailsService 계약에 연결하는 adapter.
 *
 * <p>프레임워크는 사용자 테이블, repository, role/authority entity 구조를 직접 알지 않는다.
 * 업무 프로젝트가 이 구현체에서 사용자를 조회한 뒤 프레임워크 공통 principal인
 * {@link FrameworkUserDetails}로 변환해 반환한다.</p>
 *
 * <p>이 클래스는 사용자 조회와 principal 변환까지만 담당한다.
 * password 일치 여부 판단과 인증 완료 Authentication 생성은 프레임워크의
 * AuthenticationProvider가 이어서 처리한다.</p>
 */
@Slf4j
@Service("userDetailsService")
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

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
