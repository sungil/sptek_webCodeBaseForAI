package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

/**
 * View 요청의 modelAndView와 예외 메시지를 request attribute로 보관하는 보조 interceptor.
 *
 * <p>응답 filter에서는 ModelAndView를 직접 알 수 없으므로 View 상세 로그나 에러 로그에서 사용할 정보를
 * DispatcherServlet 처리 흐름 중 request attribute로 남긴다.</p>
 */
@Slf4j
@Component
public class ViewErrorLogSupportInterceptor implements HandlerInterceptor {
    // 해당 interceptor 는 어딘가에서든? 로그 정보로 활용할 수 있도록 view 에러와 관련된 model, 및 EX message를 저장해 준다.

    /**
     * View 요청 흐름을 유지하기 위한 기본 preHandle hook이다.
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        //log.debug("preHandle called");
        return true;
    }

    /**
     * View rendering 전 ModelAndView를 request attribute에 저장한다.
     */
    @Override
    public void postHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable ModelAndView modelAndView) {
        //log.debug("postHandle called");
        //필터의 response 에서는 modelAndView 정보를 가져올 방법이 없기 때문에 이곳에서 저장해서 처리함
        request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW, modelAndView != null ? modelAndView : Collections.emptyMap());
    }

    /**
     * View 처리 중 발생한 예외 메시지를 후속 로그 처리에서 사용할 수 있게 저장한다.
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable Exception ex) {
        //log.debug("afterCompletion called");

        //api 경우는 response 내용을 통해 ex 내용을 알수 있지만 view 의 경우는 알수 없기 때문에 저장하도록 함
        //위치적으로는 viewResolver 단계에서도 에러가 날수 있기 때문에 afterCompletion 에 적용함
        //에러가 발생하는 케이스에 따라 ApplicationGlobalExceptionHandler 에서 이미 저장되는 케이스도 있음
        if (ex != null) {
            request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE, ex.getMessage());
        }
    }

}

