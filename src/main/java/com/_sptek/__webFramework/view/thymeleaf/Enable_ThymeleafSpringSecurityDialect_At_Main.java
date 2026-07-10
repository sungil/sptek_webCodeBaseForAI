package com._sptek.__webFramework.view.thymeleaf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 Thymeleaf Spring Security dialect Bean 등록을 활성화하는 애노테이션.
 *
 * <p>{@code ThymeleafConfig}가 이 애노테이션을 조건으로 dialect Bean을 등록한다.
 * Thymeleaf 템플릿에서 Spring Security 확장 속성을 사용할 때 필요한 메인 스위치이다.</p>
 */
public @interface Enable_ThymeleafSpringSecurityDialect_At_Main {
}
