package com._sptek.__webFramework.security.authentication.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * View form login 성공 후 saved request 또는 명시 redirect parameter 기준으로 이동시키는 handler.
 *
 * <p>현재는 {@link SavedRequestAwareAuthenticationSuccessHandler}의 기본 동작을 사용하되,
 * 기본 target URL과 redirect parameter 이름만 프레임워크 기준으로 설정한다.</p>
 */
@Slf4j
@Component
public class ViewFormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public final static String LOGIN_SUCCESS_TARGETURL_PARAMETER = "redirectTo";
    private final ViewLoginAuthenticationProperties properties;

    public ViewFormLoginSuccessHandler(ViewLoginAuthenticationProperties properties) {
        this.properties = properties;
        this.setDefaultTargetUrl(properties.getDefaultTargetUrl());
        this.setTargetUrlParameter(LOGIN_SUCCESS_TARGETURL_PARAMETER);
    }

    /**
     * redirectTo parameter는 같은 application 안의 context-relative URL만 허용한다.
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String targetUrlParameter = getTargetUrlParameter();
        if (StringUtils.hasText(targetUrlParameter)) {
            String requestedTargetUrl = request.getParameter(targetUrlParameter);
            if (StringUtils.hasText(requestedTargetUrl)) {
                if (ViewLoginRedirectHelper.isSafeContextRelativeRedirectUrl(requestedTargetUrl)) {
                    return requestedTargetUrl;
                }
                log.warn("Unsafe view login redirect target was ignored.");
                return properties.getDefaultTargetUrl();
            }
        }

        return super.determineTargetUrl(request, response, authentication);
    }

}
