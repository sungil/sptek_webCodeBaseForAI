package com._sptek.__webFramework.security.authentication.view;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

class ViewFormLoginSuccessHandlerTest {

    @Test
    void onAuthenticationSuccessAllowsContextRelativeRedirectToParameter() throws Exception {
        ViewLoginAuthenticationProperties properties = new ViewLoginAuthenticationProperties();
        ViewFormLoginSuccessHandler handler = new ViewFormLoginSuccessHandler(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/view/loginProcess");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter(ViewFormLoginSuccessHandler.LOGIN_SUCCESS_TARGETURL_PARAMETER, "/view/index");

        handler.onAuthenticationSuccess(request, response, new TestingAuthenticationToken("user", null));

        assertThat(response.getRedirectedUrl()).isEqualTo("/view/index");
    }

    @Test
    void onAuthenticationSuccessRejectsAbsoluteRedirectToParameter() throws Exception {
        ViewLoginAuthenticationProperties properties = new ViewLoginAuthenticationProperties();
        ViewFormLoginSuccessHandler handler = new ViewFormLoginSuccessHandler(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/view/loginProcess");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter(ViewFormLoginSuccessHandler.LOGIN_SUCCESS_TARGETURL_PARAMETER, "https://example.com");

        handler.onAuthenticationSuccess(request, response, new TestingAuthenticationToken("user", null));

        assertThat(response.getRedirectedUrl()).isEqualTo("/");
    }

    @Test
    void onAuthenticationSuccessRejectsProtocolRelativeRedirectToParameter() throws Exception {
        ViewLoginAuthenticationProperties properties = new ViewLoginAuthenticationProperties();
        ViewFormLoginSuccessHandler handler = new ViewFormLoginSuccessHandler(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/view/loginProcess");
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setParameter(ViewFormLoginSuccessHandler.LOGIN_SUCCESS_TARGETURL_PARAMETER, "//example.com");

        handler.onAuthenticationSuccess(request, response, new TestingAuthenticationToken("user", null));

        assertThat(response.getRedirectedUrl()).isEqualTo("/");
    }
}
