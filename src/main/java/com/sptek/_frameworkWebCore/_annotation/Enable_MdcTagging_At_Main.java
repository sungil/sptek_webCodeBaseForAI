package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 요청 처리 중 MDC 태그를 구성하는 필터 등록을 활성화하는 애노테이션.
 *
 * <p>{@code FilterConfigForFrameworkWebCore}가 이 애노테이션을 조건으로 {@code MakeMdcFilter}를 등록한다.
 * 로그 포맷에서 MDC 값을 사용할 때 요청 단위 추적 정보를 제공하는 스위치이다.</p>
 */
public @interface Enable_MdcTagging_At_Main {
}
