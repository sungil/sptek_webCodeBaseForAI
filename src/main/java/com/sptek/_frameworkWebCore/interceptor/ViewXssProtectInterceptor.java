package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore._annotation.Enable_XssProtectForView_At_ControllerMethod;
import com.sptek._frameworkWebCore.support.XssEscapeSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

//@HasAnnotationOnMain_InBean(TestAnnotation_InAll.class) //HasAnnotationOnMain 설정 으로 처리 하려다 성능 및 원본 수정등의 상황을 고려 하여 controller Annotation 적용 으로 변경함
/**
 * View Controller 메서드가 반환한 ModelAndView model 값을 XSS escape 처리하는 interceptor.
 *
 * <p>{@link Enable_XssProtectForView_At_ControllerMethod}가 붙은 handler method에만 적용한다.
 * API 응답이 아니라 Thymeleaf View model에 담긴 객체 escape를 담당한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Component

public class ViewXssProtectInterceptor implements HandlerInterceptor {
    private final XssEscapeSupport xssEscapeSupport;

    /**
     * 대상 View handler의 model 값을 렌더링 전에 escape 처리한다.
     */
    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            ModelAndView modelAndView
    ) {
        if (handler instanceof HandlerMethod handlerMethod && modelAndView != null) {
            if (handlerMethod.hasMethodAnnotation(Enable_XssProtectForView_At_ControllerMethod.class)) {
                log.debug("ModelView Xss Protector On");
                Map<String, Object> model = modelAndView.getModel();
                model.replaceAll((key, value) -> xssEscapeSupport.escape(value));
            }
        }
    }
}
