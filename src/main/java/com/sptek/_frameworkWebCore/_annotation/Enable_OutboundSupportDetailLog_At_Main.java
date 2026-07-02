package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 {@code OutboundSupport}의 외부 호출 상세 로그 출력을 활성화하는 애노테이션.
 *
 * <p>{@code OutboundSupport}가 요청/응답 상세 로그 활성 여부와 함께 이 애노테이션을 확인해
 * outbound 호출의 상세 로그를 남길지 결정한다.</p>
 */
public @interface Enable_OutboundSupportDetailLog_At_Main {

    /**
     * outbound 상세 로그에 전달할 선택적 태그 값.
     */
    String value() default "";
}

