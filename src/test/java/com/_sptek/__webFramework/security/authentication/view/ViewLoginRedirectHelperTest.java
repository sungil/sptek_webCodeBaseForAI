package com._sptek.__webFramework.security.authentication.view;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ViewLoginRedirectHelperTest {

    @Test
    void isSafeContextRelativeRedirectUrlAllowsOnlyLocalPath() {
        assertThat(ViewLoginRedirectHelper.isSafeContextRelativeRedirectUrl("/view/index")).isTrue();
        assertThat(ViewLoginRedirectHelper.isSafeContextRelativeRedirectUrl("/view/index?tab=1")).isTrue();
        assertThat(ViewLoginRedirectHelper.isSafeContextRelativeRedirectUrl("https://example.com")).isFalse();
        assertThat(ViewLoginRedirectHelper.isSafeContextRelativeRedirectUrl("//example.com")).isFalse();
        assertThat(ViewLoginRedirectHelper.isSafeContextRelativeRedirectUrl("/\\example")).isFalse();
    }

    @Test
    void getRedirectUrlAfterLoggingDoesNotStoreConfiguredExcludedRefererPath() {
        ViewLoginAuthenticationProperties properties = new ViewLoginAuthenticationProperties();
        properties.setNotRedirectUrls(new ArrayList<>(List.of("/view/login", "/view/signup")));
        ViewLoginRedirectHelper helper = new ViewLoginRedirectHelper(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/login");
        request.addHeader("referer", "https://local.test/view/signup");

        String redirectUrl = helper.getRedirectUrlAfterLogging(request, new MockHttpServletResponse());

        assertThat(redirectUrl).isEmpty();
    }

    @Test
    void getRedirectUrlAfterLoggingStoresSafeRefererPath() {
        ViewLoginAuthenticationProperties properties = new ViewLoginAuthenticationProperties();
        ViewLoginRedirectHelper helper = new ViewLoginRedirectHelper(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/view/login");
        request.addHeader("referer", "https://local.test/view/example/list?page=1");

        String redirectUrl = helper.getRedirectUrlAfterLogging(request, new MockHttpServletResponse());

        assertThat(redirectUrl).isEqualTo("/view/example/list?page=1");
    }
}
