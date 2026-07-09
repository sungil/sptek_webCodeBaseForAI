package com.sptek._projectCommon.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Order(10)
@Component
public class ExampleAspect {
    @Pointcut(
            "@within(org.springframework.web.bind.annotation.RestController) && " +
                    "@annotation(com.sptek.__webFramework.bootstrap.testSupport.TestAnnotation_At_All)"
    ) // ex 요건
    public void pointCut() {}

    @Around("pointCut()")
    public Object pointCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("1. around star");

        Signature signature = joinPoint.getSignature();
        log.debug("메서드 이름: {}", signature.getName());
        log.debug("선언 타입명: {}", signature.getDeclaringTypeName());

        if (signature instanceof MethodSignature methodSignature) {
            log.debug("리턴 타입: {}", methodSignature.getReturnType().getSimpleName());
            Method method = methodSignature.getMethod();
            log.debug("실행 메서드: {}", method);
            if (method.isAnnotationPresent(Deprecated.class)) log.debug("@Deprecated 존재함");
        }

        log.debug("타겟 객체 : {}", joinPoint.getTarget().getClass().getName());
        log.debug("프록시 객체 : {}", joinPoint.getThis().getClass().getName());
        log.debug("kind : {}", joinPoint.getKind());

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            log.debug("파라미터[{}] 값: {}", i, args[i]);
        }

        Object result = joinPoint.proceed();
        log.debug("4. around after joinPoint.proceed()");
        return result;
    }

    @Before("pointCut()")
    public void pointCutBefore(JoinPoint joinPoint) {
        log.debug("2. before (다른 AOP 의 Around 시작, 해당 AOP 의 모든 처리를 다 끝내고 다시 복귀)");
        //to do what you need.
    }

    @After("pointCut()")
    public void pointCutAfter(JoinPoint joinPoint) {
        log.debug("3. after");
        //to do what you need.
    }
}
