package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore._annotation.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
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

@Component
@RequiredArgsConstructor
public class ReqResDetailLogDecisionInterceptor implements HandlerInterceptor {
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED, false);
            return true;
        }

        // ReqResDetailLogFilter 는 body 캡처를 위해 DispatcherServlet 전에 동작한다.
        // 실제 로그 대상 여부는 Spring 이 HandlerMethod 를 확정한 이 인터셉터에서 request attribute 로 전달한다.
        Map<String, Object> controllerAttributes = requestMappingAnnotationRegister
                .getAnnotationAttributes(handlerMethod, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                .orElse(Map.of());

        boolean enabled = MainClassAnnotationRegister.hasAnnotation(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                || !controllerAttributes.isEmpty();

        request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED, enabled);
        request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG, resolveLogTag(controllerAttributes));
        return true;
    }

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
