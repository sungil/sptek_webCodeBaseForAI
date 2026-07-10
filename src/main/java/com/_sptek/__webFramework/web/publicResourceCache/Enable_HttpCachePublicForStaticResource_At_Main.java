package com._sptek.__webFramework.web.publicResourceCache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 정적 리소스 응답에 public cache 정책을 적용하도록 표시하는 애노테이션.
 *
 * <p>{@code ResourceHandlerConfig}가 이 애노테이션 존재 여부에 따라 cache control 이 포함된
 * resource handler와 기본 resource handler 등록을 분기한다.</p>
 */
public @interface Enable_HttpCachePublicForStaticResource_At_Main {
}
