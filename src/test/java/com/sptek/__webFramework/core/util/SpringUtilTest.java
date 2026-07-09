package com.sptek.__webFramework.core.util;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpringUtilTest {
    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void requestResponseSessionOrNullReturnNullWhenNoRequestIsBound() {
        RequestContextHolder.resetRequestAttributes();

        assertThat(SpringUtil.getRequestOrNull()).isNull();
        assertThat(SpringUtil.getResponseOrNull()).isNull();
        assertThat(SpringUtil.getSessionOrNull(false)).isNull();
    }

    @Test
    void strongRequestResponseSessionMethodsKeepExistingExceptionContract() {
        RequestContextHolder.resetRequestAttributes();

        assertThatThrownBy(SpringUtil::getRequest)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No request bound to current thread");
        assertThatThrownBy(SpringUtil::getResponse)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No request bound to current thread");
        assertThatThrownBy(() -> SpringUtil.getSession(false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No request bound to current thread");
    }

    @Test
    void requestResponseSessionOrNullReturnBoundObjects() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        HttpSession session = SpringUtil.getSessionOrNull(true);

        assertThat(SpringUtil.getRequestOrNull()).isSameAs(request);
        assertThat(SpringUtil.getResponseOrNull()).isSameAs(response);
        assertThat(session).isSameAs(request.getSession(false));
    }
}
