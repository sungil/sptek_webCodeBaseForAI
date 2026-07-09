package com.sptek.__webFramework.observability.logging;

import com.sptek.__webFramework.core.constant.CommonConstants;
import com.sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;

class ReqResDetailLogDecisionInterceptorTest {
    private ReqResDetailLogDecisionInterceptor interceptor;
    private HandlerMethod handlerMethod;

    @BeforeEach
    void setUp() throws ServletException {
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("logController", LogController.class);
        applicationContext.refresh();

        RequestMappingHandlerMapping handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.setApplicationContext(applicationContext);
        handlerMapping.afterPropertiesSet();

        applicationContext.getBeanFactory().registerSingleton("requestMappingHandlerMapping", handlerMapping);
        RequestMappingAnnotationRegister register = new RequestMappingAnnotationRegister(applicationContext);
        interceptor = new ReqResDetailLogDecisionInterceptor(register);
        handlerMethod = handlerMapping.getHandlerMethods().values().stream()
                .filter(candidate -> candidate.getMethod().getName().equals("target"))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void storesDetailLogDecisionAndTagOnRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/log/target");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, handlerMethod)).isTrue();
        assertThat(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED)).isEqualTo(true);
        assertThat(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG)).isEqualTo("target-log");
    }

    @RestController
    @RequestMapping("/api/log")
    static class LogController {
        @GetMapping("/target")
        @Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod("target-log")
        String target() {
            return "ok";
        }
    }
}
