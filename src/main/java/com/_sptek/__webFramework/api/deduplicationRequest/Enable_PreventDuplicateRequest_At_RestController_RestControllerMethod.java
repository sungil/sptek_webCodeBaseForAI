package com._sptek.__webFramework.api.deduplicationRequest;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RestController 클래스나 메서드에 붙여 중복 요청 방지 인터셉터 적용 대상을 표시하는 애노테이션.
 *
 * <p>{@code PreventDuplicateRequestInterceptor}가 handler method의 클래스 또는 메서드에 이 애노테이션이 있는지 확인한다.
 * 기본 차단 시간은 프레임워크 상수를 사용하고, API별로 정책이 다르면 {@code maxMs}, {@code minMs}로 조정한다.</p>
 *
 * <p>{@code maxMs}는 요청이 시작된 뒤 처리가 아직 완료되지 않았더라도 같은 요청을 차단할 최대 시간이다.
 * 이 시간이 지나면 첫 요청이 아직 끝나지 않았어도 같은 요청을 다시 허용해 무한 차단을 피한다.
 * {@code minMs}는 요청 처리가 완료된 뒤에도 같은 요청을 계속 차단할 최소 시간이다.
 * 빠르게 끝나는 API에서 더블클릭이나 짧은 연속 호출을 막는 용도로 사용한다.</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod {
    long maxMs() default DeduplicationRequestConstants.DUPLICATION_PREVENT_MAX_MS;
    long minMs() default DeduplicationRequestConstants.DUPLICATION_PREVENT_MIN_MS;
}
