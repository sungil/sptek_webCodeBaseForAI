package com._sptek.__webFramework.security.authentication.view;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ViewFormLoginFailureHandlerTest {

    @Test
    void onAuthenticationFailureUsesSameFailureCodeForBadCredentials() throws Exception {
        ViewLoginAuthenticationProperties properties = new ViewLoginAuthenticationProperties();
        ViewFormLoginFailureHandler handler = new ViewFormLoginFailureHandler(properties);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(new MockHttpServletRequest(), response, new BadCredentialsException("bad"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/view/login?error=EX_AUTH_FAILED");
    }

    @Test
    void onAuthenticationFailureUsesSameFailureCodeForUsernameNotFound() throws Exception {
        ViewLoginAuthenticationProperties properties = new ViewLoginAuthenticationProperties();
        ViewFormLoginFailureHandler handler = new ViewFormLoginFailureHandler(properties);
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationFailure(new MockHttpServletRequest(), response, new UsernameNotFoundException("missing"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/view/login?error=EX_AUTH_FAILED");
    }
}
