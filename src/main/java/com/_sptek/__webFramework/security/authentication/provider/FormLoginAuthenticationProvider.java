package com._sptek.__webFramework.security.authentication.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * View form login에서 전달된 username/password를 검증하는 프레임워크 AuthenticationProvider.
 *
 * <p>Spring Security 흐름에서 AuthenticationProvider는 "제출된 인증 요청이 맞는지"를 판단하는 위치다.
 * 사용자 저장소 구조는 업무 프로젝트마다 다를 수 있으므로, 사용자 조회는 UserDetailsService에 위임하고
 * 이 provider는 UserDetails 계약과 password 검증만 바라본다.</p>
 *
 * <p>검증 성공 후 반환한 Authentication은 provider가 직접 SecurityContextHolder에 넣지 않는다.
 * AuthenticationManager와 form login filter가 이후 SecurityContext/session 저장을 이어서 처리한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class FormLoginAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * email을 username으로 간주해 사용자 정보를 조회하고 password hash를 검증한다.
     *
     * <p>인자로 들어오는 Authentication은 아직 인증 완료 상태가 아니라 사용자가 제출한 username/password 묶음이다.
     * 성공 시 반환하는 Authentication의 principal에는 업무 User entity가 아니라 UserDetailsService가 만든
     * 프레임워크 공통 UserDetails 구현체가 들어간다.</p>
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        // email 값을 Name(일반적id개념) 으로 사용하는 케이스
        String username = usernamePasswordAuthenticationToken.getName();
        String password = getSubmittedPassword(usernamePasswordAuthenticationToken);
        log.debug("requested login id : {}", username);

        // 해당 계정이 존재하는지 확인(password 확인은 하지 않음)
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // password 가 일치 하지 않으면 BadCredentialsException 처리
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.debug("Password does not match.");
            throw new BadCredentialsException("Bad credentials");
        } else {
            log.debug("Password matched.");
        }
        log.debug("user info from userDetailsService by userName : {}, {}", userDetails.getUsername(), userDetails.getAuthorities());

        // principal은 이후 SecurityContext에서 "현재 사용자"로 꺼내 쓰이는 객체다.
        // 여기서는 세션 로그인과 JWT 인증을 같은 형태로 다루기 위해 UserDetailsService가 만든 공통 principal을 그대로 넣는다.
        // 성공 Authentication에는 제출된 raw password를 다시 담지 않는다.
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    /**
     * form login이 사용하는 UsernamePasswordAuthenticationToken만 처리 대상으로 선언한다.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // AuthenticationManager는 등록된 provider 중 supports()가 true인 provider에게 인증 처리를 위임한다.
        // form login은 username/password 요청을 UsernamePasswordAuthenticationToken으로 표현한다.
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String getSubmittedPassword(UsernamePasswordAuthenticationToken authentication) {
        Object credentials = authentication.getCredentials();
        if (credentials instanceof String password && !password.isBlank()) {
            return password;
        }
        throw new BadCredentialsException("Bad credentials");
    }
}
