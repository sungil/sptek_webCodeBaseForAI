package com._sptek.__webFramework.observability.timing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 요청/응답 상세 로그의 요청 시작/응답 시각/경과 시간 출력을 활성화하는 애노테이션.
 *
 * <p>{@link ExecutionTimeSupport}의 명시적 코드 블록 측정 기능에는 영향을 주지 않는다.
 * 이 애노테이션은 상세 로그에 requestTime, responseTime, durationMsec 항목을 노출할지 결정한다.</p>
 */
public @interface Enable_ExecutionTimeLog_At_Main {
}
