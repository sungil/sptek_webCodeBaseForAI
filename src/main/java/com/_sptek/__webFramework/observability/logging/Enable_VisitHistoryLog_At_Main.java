package com._sptek.__webFramework.observability.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 방문 이력 로깅 인터셉터를 활성화하는 애노테이션.
 *
 * <p>{@code VisitHistoryLoggingInterceptor}가 이 애노테이션을 조건으로 등록되고,
 * 요청 처리 시 {@code value} 속성을 로그 태그로 읽는다.</p>
 */
public @interface Enable_VisitHistoryLog_At_Main {

    /**
     * 방문 이력 로그에 전달할 선택적 태그 값.
     */
    String value() default "";
}
