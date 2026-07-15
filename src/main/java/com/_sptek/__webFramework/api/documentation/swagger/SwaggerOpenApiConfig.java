package com._sptek.__webFramework.api.documentation.swagger;

import com._sptek.__webFramework.application.info.ApplicationInfoProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
//@Profile(value = {"local", "dev", "stg"})
//@HasAnnotationOnMain_InBean(EnableSwaggerOpenApi_InMain.class)
/**
 * SpringDoc OpenAPI 문서의 기본 메타 정보를 프로젝트 설정값으로 채우는 설정.
 *
 * <p>Swagger UI와 api-docs 노출 여부는 SpringDoc 설정값에서 제어하고,
 * 이 클래스는 문서 title/version/description 같은 표시 정보를 구성하는 책임만 가진다.
 * 프로젝트 설정값이 없으면 프레임워크 기본값으로 문서를 생성한다.</p>
 */
@RequiredArgsConstructor
@Configuration
public class SwaggerOpenApiConfig {

    private final ApplicationInfoProperties applicationInfoProperties;

    /**
     * 프로젝트 정보 설정을 기반으로 OpenAPI 문서의 기본 Info 객체를 구성한다.
     *
     * <p>{@code @Profile}로 이 Bean을 제외해도 SpringDoc 기본 객체가 사용될 수 있으므로,
     * API 문서 노출 자체는 {@code springdoc.api-docs.enabled} 설정으로 제어한다.</p>
     */
    @Bean
    public OpenAPI openAPI() {
        //default url : http://localhost:8080/swagger-ui.html

        String title = "API";
        String version = "unknown";
        String description = "";

        if (applicationInfoProperties.getApp() != null) {
            title = valueOrDefault(applicationInfoProperties.getApp().getName(), title);
            version = valueOrDefault(applicationInfoProperties.getApp().getVersion(), version);
            description = valueOrDefault(applicationInfoProperties.getApp().getDescription(), description);
        }

        Info info = new Info()
                .title(title)
                .version(version)
                .description(description);
        return new OpenAPI().components(new Components()).info(info);
    }

    private String valueOrDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}
