package com._sptek._webFrameworkExample.aiExample.observability;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(10)
@Component
public class AiExampleTraceAspect {

    @Around("within(com._sptek._webFrameworkExample.aiExample.feature..*)")
    public Object traceFeatureMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("[aiExample] start {}", joinPoint.getSignature().toShortString());
        Object result = joinPoint.proceed();
        log.debug("[aiExample] end {}", joinPoint.getSignature().toShortString());
        return result;
    }
}
