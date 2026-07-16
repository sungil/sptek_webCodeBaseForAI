package com._sptek.__webFramework.web.locale;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * locale/timezone cookie와 message source 정책을 yml에서 주입받는 설정.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.locale")
public class LocaleProperties {
    private MessageSource messageSource = new MessageSource();
    private Cookie cookie = new Cookie();
    private DefaultValue defaultValue = new DefaultValue();
    private List<SupportedLocale> supportedLocales = new ArrayList<>();

    @Data
    public static class MessageSource {
        private String basename = "classpath:/messages";
        private String encoding = "UTF-8";
        private int cacheSeconds = 600;
    }

    @Data
    public static class Cookie {
        private String localeName = LocaleConstants.DEFAULT_LOCALE_COOKIE_NAME;
        private String timezoneName = LocaleConstants.DEFAULT_TIMEZONE_COOKIE_NAME;
        private int maxAgeDays = LocaleConstants.DEFAULT_COOKIE_MAX_AGE_DAY;
        private boolean httpOnly = true;
        private boolean secure = false;
        private String sameSite = "Lax";
    }

    @Data
    public static class DefaultValue {
        private String locale = "ko-KR";
        private String timezone = "Asia/Seoul";
    }

    @Data
    public static class SupportedLocale {
        private String languageCode;
        private String countryCode;
        private String timezone;

        public String getQueryString() {
            return String.format("locale=%s-%s&timezone=%s", languageCode, countryCode, timezone);
        }
    }
}
