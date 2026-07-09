package com.sptek.__webFramework.web.xss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 API 응답의 XSS 문자열 처리를 ObjectMapper 수준에서 일괄 적용하는 애노테이션.
 *
 * <p>{@code ObjectMapperConfig}가 이 애노테이션 존재 여부에 따라 XSS 처리 ObjectMapper를 등록한다.
 * 이 전역 설정이 켜지면 메서드 단위 {@code Enable_XssProtectForApi_At_ControllerMethod} 처리는 사용되지 않는다.</p>
 */
public @interface Enable_XssProtectForApi_At_Main {
}
