package com._sptek.__webFramework.web.xss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * RestController 메서드에 붙여 해당 API 응답에 선택적 XSS 문자열 처리를 적용하도록 표시하는 애노테이션.
 *
 * <p>{@code ControllerAdviceForApiXssProtect}가 메서드 애노테이션을 확인해 응답 값을 후처리한다.
 * 메인 클래스에 {@code Enable_XssProtectForApi_At_Main}이 적용된 경우에는 ObjectMapper 기반 일괄 처리가 우선한다.</p>
 */
public @interface Enable_XssProtectForApi_At_ControllerMethod {
}
