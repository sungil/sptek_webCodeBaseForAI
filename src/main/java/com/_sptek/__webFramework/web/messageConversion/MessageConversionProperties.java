package com._sptek.__webFramework.web.messageConversion;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring MVC HTTP message converter 정책을 yml에서 주입받는 설정.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.message-conversion")
public class MessageConversionProperties {
    private Json json = new Json();
    private StringConverter string = new StringConverter();

    @Data
    public static class Json {
        private boolean prettyPrint = false;
        private String defaultCharset = "UTF-8";
        private List<String> supportedMediaTypes = new ArrayList<>(List.of(
                "application/json",
                "application/*+json"
        ));
    }

    @Data
    public static class StringConverter {
        private String defaultCharset = "UTF-8";
        private boolean writeAcceptCharset = false;
    }
}
