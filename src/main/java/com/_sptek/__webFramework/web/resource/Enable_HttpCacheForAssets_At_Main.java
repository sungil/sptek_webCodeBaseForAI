package com._sptek.__webFramework.web.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 assets 전용 경로에 HTTP cache header를 적용한다.
 *
 * <p>이 애노테이션은 assets 파일명이나 참조 경로를 변환하지 않는다. 서버가 {@code /assets/**} 영역에
 * cache header를 부여하도록 {@link AssetCacheConfig}를 활성화하며, 장기 캐시 안전성은 해당 경로에
 * 배치되는 파일의 운영 정책으로 보장한다.</p>
 */
public @interface Enable_HttpCacheForAssets_At_Main {
}
