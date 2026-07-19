package com._sptek.__webFramework.security.authentication.view;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * View form login 실패 시 실패 원인을 내부 코드로 축약해 로그인 화면으로 redirect하는 handler.
 *
 * <p>보안상 상세 인증 실패 사유를 화면에 그대로 노출하지 않고, Username 미존재와 password 불일치 정도만 코드로 구분한다.</p>
 */
@Slf4j
@Component
public class ViewFormLoginFailureHandler implements AuthenticationFailureHandler {
    private final ViewLoginAuthenticationProperties properties;

    public ViewFormLoginFailureHandler(ViewLoginAuthenticationProperties properties) {
        this.properties = properties;
    }

    /**
     * Spring Security form login 실패를 로그인 화면 query parameter로 변환한다.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof UsernameNotFoundException) {
            log.debug("Username not found");

        } else if (exception instanceof BadCredentialsException) {
            log.debug("Bad credentials");

        } else {
            log.warn("Unknown view form login failure.", exception);
        }

        String redirectUrl = UriComponentsBuilder.fromPath("/view/login")
                .queryParam("error", properties.getAuthenticationFailureCode())
                .build()
                .toUriString();
        response.sendRedirect(redirectUrl);
    }
}
