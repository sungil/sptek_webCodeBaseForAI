package com._sptek.__webFramework.web.responseEscape;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;

class RestControllerMethodResponseEscapeModeResolverTest {

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void resolvesHtmlEntityModeFromRestControllerMethod() throws Exception {
        setHandlerMethod(new TestRestController(), "htmlEntity");

        assertThat(RestControllerMethodResponseEscapeModeResolver.resolve()).isEqualTo(ResponseEscapeModeEnum.HTML_ENTITY);
    }

    @Test
    void resolvesUnicodeModeFromRestControllerMethod() throws Exception {
        setHandlerMethod(new TestRestController(), "unicode");

        assertThat(RestControllerMethodResponseEscapeModeResolver.resolve()).isEqualTo(ResponseEscapeModeEnum.JSON_UNICODE);
    }

    @Test
    void ignoresSameAnnotationOnNonRestControllerMethod() throws Exception {
        setHandlerMethod(new TestViewController(), "htmlEntity");

        assertThat(RestControllerMethodResponseEscapeModeResolver.resolve()).isNull();
    }

    private void setHandlerMethod(Object bean, String methodName) throws NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HandlerMethod handlerMethod = new HandlerMethod(bean, bean.getClass().getDeclaredMethod(methodName));
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, handlerMethod);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @RestController
    static class TestRestController {
        @Enable_HtmlEntityEscapeForJsonResponse_At_RestControllerMethod
        String htmlEntity() {
            return "html";
        }

        @Enable_UnicodeEscapeForJsonResponse_At_RestControllerMethod
        String unicode() {
            return "unicode";
        }
    }

    @Controller
    static class TestViewController {
        @Enable_HtmlEntityEscapeForJsonResponse_At_RestControllerMethod
        String htmlEntity() {
            return "view";
        }
    }
}
