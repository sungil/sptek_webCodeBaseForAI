package com._sptek.__webFramework.web.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 프레임워크 쿠키 생성/삭제 유틸리티가 사용할 기본 옵션을 yml에서 주입받는 설정.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.cookie")
public class CookieProperties {
    private Defaults defaults = new Defaults();

    @Data
    public static class Defaults {
        private boolean httpOnly = true;
        private boolean secure = false;
        private String path = "/";
        private String sameSite = "Lax";
    }
}
