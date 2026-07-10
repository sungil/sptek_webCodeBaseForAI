package com._sptek.__webFramework.api.response;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * RestController 반환값을 프레임워크 공통 성공 응답 구조로 감싸는 AOP.
 *
 * <p>이미 {@link HttpEntity}를 반환한 경우에는 상태 코드와 헤더를 직접 제어하는 API로 보고 그대로 통과시킨다.</p>
 */
@Aspect
@Order(1000)
@Component
public class ApiCommonResponseHelperAspect {
    @Pointcut(
            "@within(org.springframework.web.bind.annotation.RestController) && " +
                    "(" +
                    "@within(com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController) || " +
                    "@annotation(com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController)" +
                    ")"
    )
    public void pointCut() {}

    @Around("pointCut()")
    public Object pointCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof HttpEntity) return result;
        return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(result));
    }
}
