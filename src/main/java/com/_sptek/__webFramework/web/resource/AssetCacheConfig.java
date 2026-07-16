package com._sptek.__webFramework.web.resource;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * {@code /assets/**} 영역에 HTTP cache header를 적용한다.
 *
 * <p>이 설정은 {@link Enable_HttpCacheForAssets_At_Main}이 메인 클래스에 선언된 경우에만 활성화된다.
 * 일반 static resource 경로에는 장기 캐시를 적용하지 않으며, 장기 캐시가 안전한 파일만 assets 경로에
 * 배치하는 책임은 실행 업무 프로젝트와 asset build pipeline에 둔다.</p>
 */
@RequiredArgsConstructor
@Configuration
@HasAnnotationOnMain_At_Bean(Enable_HttpCacheForAssets_At_Main.class)
public class AssetCacheConfig implements WebMvcConfigurer {
    private final AssetCacheProperties assetCacheProperties;

    /**
     * assets 전용 경로에 configured max-age cache header를 등록한다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        List<String> locations = assetCacheProperties.getLocations().stream()
                .filter(StringUtils::hasText)
                .toList();
        if (!StringUtils.hasText(assetCacheProperties.getPathPattern()) || locations.isEmpty()) {
            return;
        }

        CacheControl cacheControl = CacheControl.maxAge(assetCacheProperties.getMaxAge());
        cacheControl = assetCacheProperties.isPublicCache()
                ? cacheControl.cachePublic()
                : cacheControl.cachePrivate();
        if (assetCacheProperties.isImmutable()) {
            cacheControl = cacheControl.immutable();
        }

        resourceHandlerRegistry.addResourceHandler(assetCacheProperties.getPathPattern())
                .addResourceLocations(locations.toArray(String[]::new))
                .setCacheControl(cacheControl);
    }
}
