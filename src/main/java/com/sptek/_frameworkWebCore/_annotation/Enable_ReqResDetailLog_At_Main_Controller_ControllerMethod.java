package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스, Controller 클래스, Controller 메서드에 붙여 요청/응답 상세 로그 적용 범위를 표시하는 애노테이션.
 *
 * <p>{@code ReqResDetailLogFilter}, {@code LoggingUtil}, {@code OutboundSupport}가 메인 클래스 또는 요청 매핑의
 * 이 애노테이션을 확인해 상세 로그 출력 여부와 로그 태그를 결정한다.</p>
 */
public @interface Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod {

    /**
     * 요청/응답 상세 로그에 전달할 선택적 태그 값.
     */
    String value() default "";
}

