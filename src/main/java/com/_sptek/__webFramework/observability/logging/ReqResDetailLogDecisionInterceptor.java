package com._sptek.__webFramework.observability.logging;

import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Objects;

/**
 * 실제 HandlerMethod 기준으로 request/response 상세 로그 출력 여부를 결정하는 interceptor.
 *
 * <p>body caching은 DispatcherServlet 이전의 filter에서 수행하지만, 컨트롤러 애노테이션 판단은
 * HandlerMethod가 확정된 이 단계에서만 가능하므로 결과를 request attribute로 전달한다.</p>
 */
@Component
@RequiredArgsConstructor
public class ReqResDetailLogDecisionInterceptor implements HandlerInterceptor {
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;

    /**
     * 메인 또는 컨트롤러/메서드 애노테이션을 확인해 상세 로그 활성 여부와 로그 tag를 request에 저장한다.
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            request.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED, false);
            return true;
        }

        // ReqResDetailLogFilter 는 body 캡처를 위해 DispatcherServlet 전에 동작한다.
        // 실제 로그 대상 여부는 Spring 이 HandlerMethod 를 확정한 이 인터셉터에서 request attribute 로 전달한다.
        Map<String, Object> controllerAttributes = requestMappingAnnotationRegister
                .getAnnotationAttributes(handlerMethod, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                .orElse(Map.of());

        boolean enabled = MainClassAnnotationRegister.hasAnnotation(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                || !controllerAttributes.isEmpty();

        request.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED, enabled);
        request.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG, resolveLogTag(controllerAttributes));
        return true;
    }

    /**
     * 컨트롤러 애노테이션의 tag가 있으면 우선 사용하고, 없으면 메인 애노테이션 tag를 사용한다.
     */
    private String resolveLogTag(Map<String, Object> controllerAttributes) {
        String controllerLogTag = Objects.toString(controllerAttributes.get("value"), "");
        if (StringUtils.hasText(controllerLogTag)) {
            return controllerLogTag;
        }
        return Objects.toString(MainClassAnnotationRegister
                .getAnnotationAttributes(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                .get("value"), "");
    }
}
