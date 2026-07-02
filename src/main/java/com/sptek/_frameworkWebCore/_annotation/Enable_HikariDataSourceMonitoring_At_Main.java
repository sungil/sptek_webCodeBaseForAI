package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 Hikari datasource 상태 모니터링 스케줄러와 전용 scheduler executor Bean을 활성화하는 애노테이션.
 *
 * <p>{@code SchedulerForHikariDataSourceMonitoring}과 {@code SpecificSchedulerExecutorConfig}가
 * {@code HasAnnotationOnMain_At_Bean} 조건으로 이 애노테이션을 확인한다.</p>
 */
public @interface Enable_HikariDataSourceMonitoring_At_Main {

    /**
     * 모니터링 로그에 전달할 선택적 태그 값.
     */
    String value() default "";
}
