package com.sptek.__webFramework.observability.processTime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 프레임워크 실행 시간 측정 유틸리티의 출력 여부를 활성화하는 애노테이션.
 *
 * <p>{@code Timer}가 {@code MainClassAnnotationRegister}를 통해 이 애노테이션 존재 여부를 캐시해 확인한다.
 * 측정 구간 자체가 아니라 측정 결과를 프레임워크 기준으로 노출할지 결정하는 스위치로 사용한다.</p>
 */
public @interface Enable_ExecutionTimer_At_Main {
}
