package com._sptek.__webFramework.web.locale;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 locale/timezone cookie 기반 지역화 지원을 활성화하는 애노테이션.
 *
 * <p>{@code LocaleConfig}가 이 애노테이션을 조건으로 LocaleResolver, locale 변경 interceptor,
 * MessageSource를 등록한다.</p>
 */
public @interface Enable_LocaleSupport_At_Main {
}
