package com.sptek._frameworkWebCore.springSecurity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * View form login 실패 시 실패 원인을 내부 코드로 축약해 로그인 화면으로 redirect하는 handler.
 *
 * <p>보안상 상세 인증 실패 사유를 화면에 그대로 노출하지 않고, Username 미존재와 password 불일치 정도만 코드로 구분한다.</p>
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandlerForView implements AuthenticationFailureHandler {

    /**
     * Spring Security form login 실패를 로그인 화면 query parameter로 변환한다.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String exCode = "000";

        if (exception instanceof UsernameNotFoundException) {
            log.error("Username not found");
            exCode = "EX001";

        } else if (exception instanceof BadCredentialsException) {
            log.error("Bad credentials");
            exCode = "EX002";

        } else {
            log.error("Unknown exception");
            exCode = "EX000";
        }

        //todo : exception 케이스별로 메시지를 내릴수도 있지만 보안상의 이유로 안내려주는게 더 안전하니 적절히 판단 필요
        response.sendRedirect("/view/login?error=" + exCode);
    }
}
