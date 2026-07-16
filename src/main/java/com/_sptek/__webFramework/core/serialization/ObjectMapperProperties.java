package com._sptek.__webFramework.core.serialization;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 프레임워크 표준 ObjectMapper 직렬화/역직렬화 정책을 yml에서 주입받는 설정.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.object-mapper")
public class ObjectMapperProperties {
    private boolean failOnUnknownProperties = false;
    private boolean includeNonNullOnly = true;
    private String defaultLocale = "system";
    private String defaultTimezone = "system";
}
