package com._sptek.__webFramework.web.locale;

/**
 * Locale/TimeZone 쿠키 기반 지역화 기능에서 사용하는 파라미터와 쿠키 정책 상수.
 */
public final class LocaleConstants {
    public static final String DEFAULT_LOCALE_COOKIE_NAME = "locale";
    public static final String DEFAULT_TIMEZONE_COOKIE_NAME = "timezone";
    public static final int DEFAULT_COOKIE_MAX_AGE_DAY = 7;

    private LocaleConstants() {
    }
}
