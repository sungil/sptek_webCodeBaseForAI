package com.sptek._frameworkWebCore.base.constant;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncController_At_RestControllerMethod;
import com.sptek._frameworkWebCore._annotation.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod;
import com.sptek._frameworkWebCore._annotation.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingAnnotationRegisterTest {
    private RequestMappingAnnotationRegister register;
    private RequestMappingHandlerMapping handlerMapping;

    @BeforeEach
    void setUp() throws ServletException {
        StaticApplicationContext applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton("testController", TestController.class);
        applicationContext.refresh();

        handlerMapping = new RequestMappingHandlerMapping();
        handlerMapping.setApplicationContext(applicationContext);
        handlerMapping.setServletContext(new MockServletContext());
        handlerMapping.afterPropertiesSet();

        register = new RequestMappingAnnotationRegister(handlerMapping);
    }

    @Test
    void methodAnnotationOverridesClassAnnotationAttributes() {
        HandlerMethod methodHandler = findHandler("methodTag");

        assertThat(register.hasAnnotation(methodHandler, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)).isTrue();
        assertThat(register.getAnnotationAttributes(methodHandler, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class))
                .contains(Map.of("value", "method-tag"));
    }

    @Test
    void samePathDifferentConsumesKeepsEachHandlerMethodMetadata() {
        HandlerMethod jsonHandler = findHandler("samePathJson");
        HandlerMethod formHandler = findHandler("samePathForm");

        assertThat(register.hasAnnotation(jsonHandler, Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod.class)).isTrue();
        assertThat(register.hasAnnotation(jsonHandler, Enable_AsyncController_At_RestControllerMethod.class)).isFalse();

        assertThat(register.hasAnnotation(formHandler, Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod.class)).isFalse();
        assertThat(register.hasAnnotation(formHandler, Enable_AsyncController_At_RestControllerMethod.class)).isTrue();
    }

    @Test
    void requestLookupUsesResolvedHandlerMethodInsteadOfRequestUri() {
        HandlerMethod jsonHandler = findHandler("samePathJson");
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/context/api/test/same-path");
        request.setContextPath("/context");
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, jsonHandler);

        assertThat(register.hasAnnotation(request, Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod.class)).isTrue();
        assertThat(register.hasRequestMapping(request)).isTrue();
    }

    @Test
    void requestLookupReturnsFalseWhenSpringDidNotResolveHandler() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/unknown");

        assertThat(register.hasAnnotation(request, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)).isFalse();
        assertThat(register.hasRequestMapping(request)).isFalse();
    }

    private HandlerMethod findHandler(String methodName) {
        return handlerMapping.getHandlerMethods().values().stream()
                .filter(handlerMethod -> handlerMethod.getMethod().getName().equals(methodName))
                .findFirst()
                .orElseThrow();
    }

    @RestController
    @RequestMapping("/api/test")
    @Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod("class-tag")
    static class TestController {
        @PostMapping("/method-tag")
        @Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod("method-tag")
        String methodTag() {
            return "ok";
        }

        @PostMapping(value = "/same-path", consumes = MediaType.APPLICATION_JSON_VALUE)
        @Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod
        String samePathJson() {
            return "json";
        }

        @PostMapping(value = "/same-path", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        @Enable_AsyncController_At_RestControllerMethod
        String samePathForm() {
            return "form";
        }
    }
}
