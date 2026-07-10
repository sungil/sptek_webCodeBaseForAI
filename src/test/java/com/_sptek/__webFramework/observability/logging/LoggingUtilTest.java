package com._sptek.__webFramework.observability.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingUtilTest {
    @Test
    void reqResDetailLoggingDoesNotCreateSessionWhenOneDoesNotExist() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/not-found-page");
        MockHttpServletResponse response = new MockHttpServletResponse();

        LoggingUtil.reqResDetailLogging(LoggerFactory.getLogger(LoggingUtilTest.class), request, response, "test");

        assertThat(request.getSession(false)).isNull();
    }
}
