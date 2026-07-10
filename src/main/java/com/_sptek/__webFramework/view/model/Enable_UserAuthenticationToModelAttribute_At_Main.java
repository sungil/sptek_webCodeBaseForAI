package com._sptek.__webFramework.view.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 현재 인증 사용자 정보를 View 모델 속성으로 주입하는 ControllerAdvice를 활성화하는 애노테이션.
 *
 * <p>{@code ControllerAdviceForUserAuthenticationToModelAttribute}가 이 애노테이션을 조건으로 등록된다.
 * Thymeleaf 화면에서 로그인 사용자 정보를 공통 모델 값으로 참조해야 할 때 사용한다.</p>
 */
public @interface Enable_UserAuthenticationToModelAttribute_At_Main {
}
