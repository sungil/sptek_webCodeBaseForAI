package com.sptek._frameworkWebCore.springSecurity.spt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/*
AuthenticationProvider와 UserDetailsService를 만드는 주체가 서로 다를 수 있다는 관점에서 이해해야 함
(왜 이런 여러 단계를 두었을까에 대한 고민이 필요함).

그래서 AuthenticationProvider는 실질적인 인증 처리를 위해 UserDetailsService를 주입받는 것이고,
또 서로 다른 주체 간에 인터페이스를 맞추기 위해 UserDetailsService의 loadUserByUsername 리턴 타입을 UserDetails로 규정해 놓은 것임.

예를 들어,
인증 정책은 통합하여 한 곳(AuthenticationProvider)에서 만들고 내려주지만,
서비스별로 User 테이블의 컬럼 이름이나 구조가 다를 수 있음.
(예: 여기서는 id에 해당하는 값을 email로 사용)

따라서 UserDetailsService는 서비스별로 각자 구현하고,
최종적으로 UserDetails 규격만 맞춰서 리턴하면
AuthenticationProvider 입장에서는 통일된 방식으로 처리할 수 있음.
*/
/**
 * View form login에서 전달된 username/password를 검증하는 프레임워크 AuthenticationProvider.
 *
 * <p>사용자 조회는 UserDetailsService에 위임하고, password 검증은 BCryptPasswordEncoder로 수행한다.
 * 검증 성공 후 반환한 Authentication은 AuthenticationManager와 security filter가 SecurityContext에 저장한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * email을 username으로 간주해 사용자 정보를 조회하고 password hash를 검증한다.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        // email 값을 Name(일반적id개념) 으로 사용하는 케이스
        String email = usernamePasswordAuthenticationToken.getName();
        String password = (String) usernamePasswordAuthenticationToken.getCredentials();
        log.debug("requested id, ps : {}, {}", email, bCryptPasswordEncoder.encode(password));

        // 해당 계정이 존재하는지 확인(password 확인은 하지 않음)
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
        log.debug("user info from userDetailsService by userName : {}, {}, {}", customUserDetails.getUsername(), customUserDetails.getPassword(), customUserDetails.getAuthorities());

        // password 가 일치 하지 않으면 BadCredentialsException 처리
        if (!bCryptPasswordEncoder.matches(password, customUserDetails.getPassword())) {
            log.debug("Password does not match.");
            throw new BadCredentialsException(customUserDetails.getUsername() + " : Password does not match.");
        } else {
            log.debug("Password matched.");
        }

        // todo : principal 로 customUserDetails 전체를 주는게 맞을까? name만 주는게 맞을까?(principal 은 Object 타입으로 서비스상 필요한 적절한 객체로 전달하면 됨으로.. 일단 전체 정보인 customUserDetails로 넘김)
        // Provider는 생성한 Authentication을 리턴할뿐 SecurityContextHolder에 저장하는 역할은 하지 않아야 함 (Provider가 여럿 설정된 상황일수 있으니 AuthenticationManager 에게 맞겨야함)
        // 비밀번호는 매칭 확인 후 노출을 막기 위에 변형 처리함 (필요시 넘길수도 있겠지...)
        customUserDetails.getUserDto().setPassword("[PROTECTED]");
        // password = "[PROTECTED]"; //Authentication에 들어가는 password(credentials)은 자동 PROTECTED 처리됨

        // 리턴된 결과는 Spring Security filter 에 의해 자동으로 SecurityContextHolder.getContext().setAuthentication() 으로 설정되며
        // session 과도 자동으로 연결되어 sessionId 가 넘어오는 경우 자동으로  setAuthentication 가 이루어짐 (session destroy 시 authentication도 자동 소멸)
        return new UsernamePasswordAuthenticationToken(customUserDetails, password, customUserDetails.getAuthorities());
    }

    /**
     * form login이 사용하는 UsernamePasswordAuthenticationToken만 처리 대상으로 선언한다.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // 내가 어떤 종류의 Token을 받았을때 처리할수 있는지를 알려주는 것이다.
        // authenticationManager는 자기에 등록되어 있는 authenticationProvider 목록에서 해당 token을 지원하는 provider 들에게 인증 처리를 요청하게 된다.
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
