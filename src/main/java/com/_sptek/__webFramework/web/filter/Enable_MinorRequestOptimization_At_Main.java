package com._sptek.__webFramework.web.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 정적 리소스나 중요도가 낮은 요청의 세션 생성과 일부 필터 처리를 줄이도록 표시하는 애노테이션.
 *
 * <p>{@code FilterConfigForFrameworkWebCore}가 no-session 필터를 등록하고,
 * 여러 필터가 {@code MainClassAnnotationRegister}로 이 애노테이션을 확인해 minor request 처리 방식을 분기한다.</p>
 */
public @interface Enable_MinorRequestOptimization_At_Main {
}
