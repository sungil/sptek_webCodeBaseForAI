package com._sptek.__webFramework.observability.mdc;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class MakeMdcFilterTest {
    @Test
    void doesNotCreateSessionForStaticRequest() throws ServletException, IOException {
        MakeMdcFilter filter = new MakeMdcFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/css/app.css");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (servletRequest, servletResponse) -> {
            assertThat(((HttpServletRequest) servletRequest).getSession(false)).isNull();
            assertThat(MDC.get("sessionId")).isEmpty();
            assertThat(MDC.get("correlationId")).isNotBlank();
        };

        filter.doFilter(request, response, filterChain);

        assertThat(request.getSession(false)).isNull();
        assertThat(response.getHeader("Correlation-Id")).isNotBlank();
    }

    @Test
    void createsSessionForNonMinorRequest() throws ServletException, IOException {
        MakeMdcFilter filter = new MakeMdcFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (servletRequest, servletResponse) -> {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String maskedSessionId = httpServletRequest.getSession(false).getId().substring(0, 8) + "**";

            assertThat(httpServletRequest.getSession(false)).isNotNull();
            assertThat(MDC.get("sessionId")).isEqualTo(maskedSessionId);
        };

        filter.doFilter(request, response, filterChain);

        assertThat(request.getSession(false)).isNotNull();
    }
}
