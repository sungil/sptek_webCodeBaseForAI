package com._sptek.__webFramework.api.documentation.swagger;

import com._sptek.__webFramework.system.projectInfo.ProjectInfoVo;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
//@Profile(value = {"local", "dev", "stg"})
//@HasAnnotationOnMain_InBean(EnableSwaggerOpenApi_InMain.class)
//@EnableConfigurationProperties(ProjectInfoVo.class) //ProjectInfoVo 가 프로필에 따라 오류가 나서..
/**
 * SpringDoc OpenAPI 문서의 기본 메타 정보를 프로젝트 설정값으로 채우는 설정.
 *
 * <p>Swagger UI와 api-docs 노출 여부는 SpringDoc 설정값에서 제어하고,
 * 이 클래스는 문서 title/version/description 같은 표시 정보를 구성하는 책임만 가진다.</p>
 */
@RequiredArgsConstructor
@Configuration
public class SwaggerOpenApiConfig {

    private final ProjectInfoVo projectInfoVo;

    /**
     * 프로젝트 정보 설정을 기반으로 OpenAPI 문서의 기본 Info 객체를 구성한다.
     *
     * <p>{@code @Profile}로 이 Bean을 제외해도 SpringDoc 기본 객체가 사용될 수 있으므로,
     * API 문서 노출 자체는 {@code springdoc.api-docs.enabled} 설정으로 제어한다.</p>
     */
    @Bean
    public OpenAPI openAPI() {
        //default url : http://localhost:8080/swagger-ui.html

        Info info = new Info()
                .title(projectInfoVo.getApp().getName())
                .version(projectInfoVo.getApp().getVersion())
                .description(projectInfoVo.getApp().getDescription());
        return new OpenAPI().components(new Components()).info(info);
    }
}
