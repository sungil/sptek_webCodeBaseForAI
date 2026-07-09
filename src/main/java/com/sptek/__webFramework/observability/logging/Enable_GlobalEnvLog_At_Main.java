package com.sptek.__webFramework.observability.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 애플리케이션 시작 시 전역 환경 정보 로그 출력을 활성화하는 애노테이션.
 *
 * <p>{@code SystemGlobalEnvTemporaryValue}가 이 애노테이션을 확인한 경우에만 환경 정보 로그를 생성한다.
 * 민감 정보 노출 가능성이 있으므로 운영 적용 여부는 로그 정책과 함께 판단한다.</p>
 */
public @interface Enable_GlobalEnvLog_At_Main {

    /**
     * 환경 정보 로그에 전달할 선택적 태그 값.
     */
    String value() default "";
}
