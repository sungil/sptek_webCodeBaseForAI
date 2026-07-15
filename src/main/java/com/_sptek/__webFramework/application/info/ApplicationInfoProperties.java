package com._sptek.__webFramework.application.info;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 실행 애플리케이션의 이름, 버전, 설명을 project-info 프로퍼티에서 바인딩하는 설정 객체.
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "project-info")
public class ApplicationInfoProperties {
    private App app;

    @Data
    public static class App {
        @Schema(description = "프로젝트 이름")
        private String name;
        @Schema(description = "프로젝트 버전")
        private String version;
        @Schema(description = "프로젝트 설명")
        private String description;
    }

    @PostConstruct
    public void init() {
        log.debug("Application information from property : {}", this);
    }
}
