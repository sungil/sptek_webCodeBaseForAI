package com._sptek.__webFramework.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 실행 프로젝트별 업무/공통 코드 패키지 경계를 yml에서 주입받는 설정.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.package-boundary")
public class WebFrameworkPackageProperties {
    private List<String> applicationPackagePrefixes = new ArrayList<>();
}
