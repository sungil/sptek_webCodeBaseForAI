package com._sptek.__webFramework.web.xss;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 현재 요청을 처리한 RestController 메서드의 JSON 응답 escape 정책을 찾는다.
 */
public class RestControllerMethodResponseEscapeModeResolver {

    private RestControllerMethodResponseEscapeModeResolver() {
    }

    public static ResponseEscapeModeEnum resolve() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return null;
        }

        return resolve(handlerMethod);
    }

    private static ResponseEscapeModeEnum resolve(HandlerMethod handlerMethod) {
        if (!AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), RestController.class)) {
            return null;
        }

        // 둘 다 붙은 경우 값 자체를 표시용 문자열로 바꾸는 HTML entity 선택을 더 명시적인 정책으로 본다.
        if (handlerMethod.hasMethodAnnotation(Enable_HtmlEntityEscapeForJsonResponse_At_RestControllerMethod.class)) {
            return ResponseEscapeModeEnum.HTML_ENTITY;
        }

        if (handlerMethod.hasMethodAnnotation(Enable_UnicodeEscapeForJsonResponse_At_RestControllerMethod.class)) {
            return ResponseEscapeModeEnum.JSON_UNICODE;
        }

        return null;
    }
}
