package com.sptek.__webFramework.api.response;

import com.sptek.__webFramework.web.asyncResponse.Enable_AsyncController_At_RestControllerMethod;
import com.sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import com.sptek.__webFramework.core.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Aspect
@Order(1000)
@Component

// fw 의 api Controller 가 object 타입으로 넘긴 결과를 적절한 타입으로 변경하여 리턴 처리
// Note: 중요!!
// CompletableFuture 처리가 추가 되면서 Async 응답을 하는 경우 쓰레드 내부에서는 joinPoint.proceed()를 사용 할수가 없음
// 그래서 ReflectionUtils 을 통해 직접 해당 메소드를 호출하여 처리하고 있음
// 이 경우 joinPoint.proceed() 가 호출 되지 않았음으로 본인의 befroe, after 및 AOP 필터 체인으로도 전파되지 못함
// 그래서 해당 AOP는 AOP 필터 체인에서 가장 하위에 존재해야 정상 동작 할수 있음을 알고 사용해야 함!

public class ApiCommonResponseHelperAspect {
    private final TaskExecutor taskExecutor;
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;
    // sptTaskExecutor 는 Bean name 으로 명확히 찾고, 어노테이션 판단은 실제 HandlerMethod 기반 register 를 사용한다.
    public ApiCommonResponseHelperAspect(@Qualifier("sptTaskExecutor") TaskExecutor taskExecutor
            , RequestMappingAnnotationRegister requestMappingAnnotationRegister) {
        this.taskExecutor = taskExecutor;
        this.requestMappingAnnotationRegister = requestMappingAnnotationRegister;
    }

    @Pointcut(
            "@within(org.springframework.web.bind.annotation.RestController) && " +
                    "(" +
                    "@within(com.sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController) || " +
                    "@annotation(com.sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController)" +
                    ")"
    )
    public void pointCut() {}

    @Around("pointCut()")
    public Object pointCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (requestMappingAnnotationRegister.hasAnnotation(SpringUtil.getRequest(), Enable_AsyncController_At_RestControllerMethod.class)) {
            // AsyncResponse 적용시 컨트롤러 작업을 Async 로 처리하고 리턴 값을 CompletableFuture 로 한번더 래핑 해준다.
            // 최종 리턴 인스턴스 타입 : CompletableFuture(ResponseEntity(ApiCommonSuccessResponseDto(result)))
            return CompletableFuture.supplyAsync(() -> {
                Object target = joinPoint.getTarget();
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                // 프록시일 수 있으니 실제 구현 메서드로 보정
                method = org.springframework.aop.support.AopUtils.getMostSpecificMethod(method, target.getClass());
                Object[] args = joinPoint.getArgs();

                try {
                    //Object result = joinPoint.proceed(); -> 쓰레드 내부에서는 이렇게 처리할수 없어 아래 코드를 만듬
                    Object result = org.springframework.util.ReflectionUtils.invokeMethod(method, target, args);
                    if (result instanceof CompletableFuture<?> completableFuture) {
                        // 최종 return 때 CompletableFuture 래핑을 하기 때문에 이 시점에서는 제거
                        return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(((CompletableFuture)result).get()));
                    } else {
                        return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(result));
                    }
                } catch (Throwable throwable) {
                    throw new CompletionException(throwable);
                }
            }, taskExecutor);

        } else {
            Object result = joinPoint.proceed();
            if (result instanceof HttpEntity) { //ResponseEntity 포함
                // 이미 HttpEntity 또는 ResponseEntity 라면 수정 없이 그대로 반환
                return result;
            } else if (result instanceof CompletableFuture<?> completableFuture) {
                // todo: 두 리턴 중 어떤 방식이 더 좋을까?
                // 일반 object 방식으로 취급되어 처리
                return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(((CompletableFuture)result).get()));
                // CompletableFuture(ResponseEntity(ApiCommonSuccessResponseDto(result))) 로 변형 (Async Response 를 붙인것과 동일 처리됨)
                // return completableFuture.thenApply(obj -> ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(obj)));

            } else {
                return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(result));
            }
        }
    }
}
