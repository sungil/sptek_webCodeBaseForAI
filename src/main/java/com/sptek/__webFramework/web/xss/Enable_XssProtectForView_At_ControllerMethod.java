package com.sptek.__webFramework.web.xss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * View Controller 메서드에 붙여 요청 파라미터의 XSS 문자열 처리를 적용하도록 표시하는 애노테이션.
 *
 * <p>{@code ViewXssProtectInterceptor}가 handler method의 이 애노테이션을 확인한 뒤 요청 파라미터 값을 escape 처리한다.
 * 화면 요청 중 특정 메서드에만 view XSS 방어를 적용할 때 사용한다.</p>
 */
public @interface Enable_XssProtectForView_At_ControllerMethod {
}
