package com.sptek.__webFramework.web.error;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 애플리케이션 전역 예외 처리와 기본 오류 컨트롤러 구성을 활성화하는 애노테이션.
 *
 * <p>{@code ApplicationGlobalExceptionHandler}와 {@code CustomErrorController}가 이 애노테이션을 조건으로 등록되어
 * 프레임워크 공통 오류 응답과 오류 페이지 흐름을 제공한다.</p>
 */
public @interface Enable_ResponseOfApplicationGlobalException_At_Main {
}
