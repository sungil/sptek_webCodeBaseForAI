package com._sptek.__webFramework.web.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring MVC static resource handler가 참조할 공개 리소스 위치를 yml에서 주입받는 설정.
 *
 * <p>이 설정은 리소스 위치 매핑만 담당하며, 장기 캐시 헤더는
 * {@link AssetCacheProperties}와 {@link AssetCacheConfig}가 별도로 담당한다.</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.static-resource")
public class StaticResourceProperties {
    private List<String> locations = new ArrayList<>(List.of(
            "classpath:/static/",
            "classpath:/public/",
            "classpath:/resources/",
            "classpath:/META-INF/resources/"
    ));

    private Swagger swagger = new Swagger();
    private FrameworkJs frameworkJs = new FrameworkJs();

    @Data
    public static class Swagger {
        private String uiPathPattern = "swagger-ui.html";
        private String uiLocation = "classpath:/META-INF/resources/";
        private String webjarsPathPattern = "/webjars/**";
        private String webjarsLocation = "classpath:/META-INF/resources/webjars/";
    }

    @Data
    public static class FrameworkJs {
        private String pathPattern = "/js/_framework/**";
        private String location = "";
    }
}
