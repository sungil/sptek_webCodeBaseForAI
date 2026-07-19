package com._sptek.__webFramework.security.authentication.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * View form login 성공 후 saved request 또는 명시 redirect parameter 기준으로 이동시키는 handler.
 *
 * <p>현재는 {@link SavedRequestAwareAuthenticationSuccessHandler}의 기본 동작을 사용하되,
 * 기본 target URL과 redirect parameter 이름만 프레임워크 기준으로 설정한다.</p>
 */
@Slf4j
@Component

// 현재는 SavedRequestAwareAuthenticationSuccessHandler 의 옵션 설정 외 그데로 사용.
public class ViewFormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public final static String LOGIN_SUCCESS_TARGETURL_PARAMETER = "redirectTo";

    //초기 필요 옵션 설정
    ViewFormLoginSuccessHandler() {
        this.setDefaultTargetUrl("/");
        this.setTargetUrlParameter(LOGIN_SUCCESS_TARGETURL_PARAMETER);
    }

//    @Override
//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
//
//    }

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//
//    }

}
