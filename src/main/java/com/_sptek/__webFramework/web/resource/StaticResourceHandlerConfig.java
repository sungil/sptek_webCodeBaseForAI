package com._sptek.__webFramework.web.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 캐시 정책과 무관한 Spring MVC static resource handler를 등록한다.
 *
 * <p>이 설정은 Swagger/WebJar 보조 경로, 프레임워크 JS 공개 경로, 실행 프로젝트 static resource
 * 위치를 URL에 연결한다. HTTP cache header가 필요한 assets 경로는 {@link AssetCacheConfig}에서
 * 별도 handler로 등록한다.</p>
 */
@RequiredArgsConstructor
@Configuration
public class StaticResourceHandlerConfig implements WebMvcConfigurer {
    private final StaticResourceProperties staticResourceProperties;

    /**
     * 캐시 헤더 없이 기본 static resource 위치와 보조 리소스 경로를 등록한다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        StaticResourceProperties.Swagger swagger = staticResourceProperties.getSwagger();
        StaticResourceProperties.FrameworkJs frameworkJs = staticResourceProperties.getFrameworkJs();
        List<String> locations = staticResourceProperties.getLocations().stream()
                .filter(StringUtils::hasText)
                .toList();

        if (StringUtils.hasText(swagger.getUiPathPattern()) && StringUtils.hasText(swagger.getUiLocation())) {
            resourceHandlerRegistry.addResourceHandler(swagger.getUiPathPattern())
                    .addResourceLocations(swagger.getUiLocation());
        }
        if (StringUtils.hasText(swagger.getWebjarsPathPattern()) && StringUtils.hasText(swagger.getWebjarsLocation())) {
            resourceHandlerRegistry.addResourceHandler(swagger.getWebjarsPathPattern())
                    .addResourceLocations(swagger.getWebjarsLocation());
        }
        if (StringUtils.hasText(frameworkJs.getPathPattern()) && StringUtils.hasText(frameworkJs.getLocation())) {
            resourceHandlerRegistry.addResourceHandler(frameworkJs.getPathPattern())
                    .addResourceLocations(frameworkJs.getLocation());
        }

        if (!locations.isEmpty()) {
            resourceHandlerRegistry.addResourceHandler("/**")
                    .addResourceLocations(locations.toArray(String[]::new));
        }
    }
}
