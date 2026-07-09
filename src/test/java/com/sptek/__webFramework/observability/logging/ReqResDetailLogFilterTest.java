package com.sptek.__webFramework.observability.logging;

import com.sptek.__webFramework.core.constant.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ReqResDetailLogFilterTest {
    @Test
    void copiesResponseBodyWhenDetailLogDecisionWasMadeAfterFilterWrapping() throws ServletException, IOException {
        ReqResDetailLogFilter filter = new ReqResDetailLogFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/log/target");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = (servletRequest, servletResponse) -> {
            servletRequest.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED, true);
            servletResponse.getWriter().write("ok");
        };

        filter.doFilter(request, response, filterChain);

        assertThat(response.getContentAsString()).isEqualTo("ok");
    }
}
