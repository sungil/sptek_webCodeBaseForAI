package com.sptek.__webFramework.system.projectInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/*
@ConfigurationProperties 선언을 통해 prefix 이하의 필드값을 클레스(VO) 단위로 변활 할수 있게해 준다.
프로젝트에서 자주 사용되는 프로퍼티 정보를 아래와 같이 클레스를 구성하여 필요할때 spring 주입을 통해 사용하면 된다.
 */
@Setter
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "project-info") //property에서 해당 prefix 값 이하의 항목과 클레스 내부 변수를 매핑해 준다.
public class ProjectInfoVo {
    private App app;

    @Data
    public static class App {
        @Schema(description = "프로젝트 이름") //swagger
        private String name;
        @Schema(description = "프로젝트 버전")
        private String version;
        @Schema(description = "프로젝트 설명")
        private String description;
    }

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.debug("Project information from property : {}", this);
    }
}




