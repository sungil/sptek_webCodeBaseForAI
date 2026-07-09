package com.sptek.__webFramework.web.util;

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
}
