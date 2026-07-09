package com.sptek.__webFramework.view.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 View 모델에 공통 properties 값을 주입하는 ControllerAdvice를 활성화하는 애노테이션.
 *
 * <p>{@code ControllerAdviceForPropertiesToModelAttribute}가 이 애노테이션을 조건으로 등록된다.
 * Thymeleaf 화면에서 공통 설정값을 모델 속성으로 참조해야 할 때 사용하는 메인 스위치이다.</p>
 */
public @interface Enable_PropertiesToModelAttribute_At_Main {
}
