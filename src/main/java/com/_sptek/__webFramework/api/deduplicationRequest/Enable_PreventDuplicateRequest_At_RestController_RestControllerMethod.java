package com._sptek.__webFramework.api.deduplicationRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RestController 클래스나 메서드에 붙여 중복 요청 방지 인터셉터 적용 대상을 표시하는 애노테이션.
 *
 * <p>{@code PreventDuplicateRequestInterceptor}가 handler method의 클래스 또는 메서드에 이 애노테이션이 있는지 확인한다.
 * 동일한 중복 방지 정책을 컨트롤러 전체에 적용하거나 특정 API 메서드에만 적용할 때 사용한다.</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod {
}
