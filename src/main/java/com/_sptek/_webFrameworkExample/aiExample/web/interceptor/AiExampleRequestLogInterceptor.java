package com._sptek._webFrameworkExample.aiExample.web.interceptor;

import com._sptek.__webFramework.web.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AiExampleRequestLogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler
    ) {
        if (handler instanceof HandlerMethod handlerMethod) {
            log.debug("[aiExample] {} {} -> {}.{}",
                    request.getMethod(),
                    RequestUtil.getRequestUrlQuery(request),
                    handlerMethod.getBeanType().getSimpleName(),
                    handlerMethod.getMethod().getName());
        }
        return true;
    }
}
