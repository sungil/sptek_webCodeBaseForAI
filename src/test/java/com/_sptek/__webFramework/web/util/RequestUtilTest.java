package com._sptek.__webFramework.web.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestUtilTest {
    @Test
    void getSessionAttributeReturnsNullWithoutCreatingSession() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

        Object attribute = RequestUtil.getSessionAttribute(request, "missing");

        assertThat(attribute).isNull();
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    void getSessionAttributesAllReturnsEmptyMapWhenSessionDoesNotExist() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");

        Map<String, Object> attributes = RequestUtil.getSessionAttributesAll(request, false);

        assertThat(attributes).isEmpty();
        assertThat(request.getSession(false)).isNull();
    }

    @Test
    void getReqUserIpUsesFirstValidForwardedForValue() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        request.addHeader("X-Forwarded-For", "unknown, 203.0.113.10, 10.0.0.1");
        request.setRemoteAddr("127.0.0.1");

        String ip = RequestUtil.getReqUserIp(request);

        assertThat(ip).isEqualTo("203.0.113.10");
    }

    @Test
    void getReqUserIpFallsBackToRemoteAddrWhenHeadersAreInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        request.addHeader("X-Forwarded-For", "unknown");
        request.setRemoteAddr("2001:db8:85a3::8a2e:370:7334");

        String ip = RequestUtil.getReqUserIp(request);

        assertThat(ip).isEqualTo("2001:db8:85a3::8a2e:370:7334");
    }
}
