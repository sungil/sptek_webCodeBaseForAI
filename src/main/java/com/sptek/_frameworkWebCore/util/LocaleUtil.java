package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@RequiredArgsConstructor
public class LocaleUtil {
    private static final MessageSource messageSource;
    private static List<LocaleDto> localeDtos = null;

    //static final 상수 초기화에 사용, 생성자 보다 먼저 실행됨
    static {
        messageSource = SpringUtil.getSpringBean(MessageSource.class);
    }


    public static String getI18nMessage(String code) {
        return LocaleUtil.getI18nMessage(code, null);
    }

    public static String getI18nMessage(String code, @Nullable Object arg) {
        if (arg == null) {
            return getI18nMessage(code, null);
        } else {
            return LocaleUtil.getI18nMessage(code, new Object[]{arg});
        }
    }

    //현재 Locale에 해당하는 메시지로 제공한다. (arg 가 객체로 들어올 경우 해당 객체의 toString() 이 적용됨)
    public static String getI18nMessage(String code, @Nullable Object[] args) {
        String langCode;
        HttpServletRequest request = SpringUtil.getRequestOrNull();
        if (request != null && RequestContextUtils.getLocaleResolver(request) != null) {
            langCode = LocaleContextHolder.getLocale().toLanguageTag();

        } else {
            // 로그아웃 등으로 세션이 종료되어 localeResolver 가 삭제 되더라도 locale 쿠키가 남아 있다면 locale 쿠키로 다국어 지원을 하기 위해서
            // 사실 CustomLocaleChangeInterceptor 가 등록되어 있는 경우 이 케이스는 나오지 않을 것임
            List<Cookie> localeCookie = CookieUtil.getCookies(CommonConstants.LOCALE_COOKIE_NAME);
            langCode = !localeCookie.isEmpty() ? localeCookie.get(0).getValue() : Locale.getDefault().getLanguage();
        }

        return messageSource.getMessage(code, args, Locale.forLanguageTag(langCode));
    }

    public static Locale getCurUserLocale(){
        return LocaleContextHolder.getLocale();
    }

    public static TimeZone getCurUserTimeZone(){
        return LocaleContextHolder.getTimeZone();
    }

    public static String getCurUserLanguageTag(){
        return LocaleContextHolder.getLocale().toLanguageTag();
    }

    public static String getCurUserTimeZoneName(){
        return LocaleContextHolder.getTimeZone().getDisplayName();
    }

    public static List<LocaleDto> getMajorLocales() {
        if(localeDtos == null) {
            localeDtos = Arrays.asList(
                    // 미국 (US) - 4개 주요 타임존
                    new LocaleDto("en", "US", "America/New_York"),  // 동부 (EST)
                    new LocaleDto("en", "US", "America/Chicago"),   // 중부 (CST)
                    new LocaleDto("en", "US", "America/Denver"),    // 산악 (MST)
                    new LocaleDto("en", "US", "America/Los_Angeles"), // 태평양 (PST)

                    // 캐나다 (CA) - 여러 개의 타임존
                    new LocaleDto("en", "CA", "America/Toronto"),   // 동부 (EST)
                    new LocaleDto("en", "CA", "America/Winnipeg"),  // 중부 (CST)
                    new LocaleDto("en", "CA", "America/Edmonton"),  // 산악 (MST)
                    new LocaleDto("en", "CA", "America/Vancouver"), // 태평양 (PST)

                    // 러시아 (RU) - 4개 주요 타임존
                    new LocaleDto("ru", "RU", "Europe/Moscow"),     // 모스크바 시간 (MSK)
                    new LocaleDto("ru", "RU", "Asia/Yekaterinburg"), // 예카테린부르크 시간 (YEKT)
                    new LocaleDto("ru", "RU", "Asia/Novosibirsk"),  // 노보시비르스크 시간 (NOVT)
                    new LocaleDto("ru", "RU", "Asia/Vladivostok"),  // 블라디보스토크 시간 (VLAT)

                    // 브라질 (BR) - 3개 주요 타임존
                    new LocaleDto("pt", "BR", "America/Sao_Paulo"), // 브라질리아 시간 (BRT)
                    new LocaleDto("pt", "BR", "America/Manaus"),    // 아마존 시간 (AMT)
                    new LocaleDto("pt", "BR", "America/Noronha"),   // 페르난두디노로냐 시간 (FNT)

                    // 호주 (AU) - 3개 주요 타임존
                    new LocaleDto("en", "AU", "Australia/Sydney"),  // 동부 표준시 (AEST)
                    new LocaleDto("en", "AU", "Australia/Adelaide"), // 중부 표준시 (ACST)
                    new LocaleDto("en", "AU", "Australia/Perth"),   // 서부 표준시 (AWST)

                    // 중국 (CN) - 단일 시간대
                    new LocaleDto("zh", "CN", "Asia/Shanghai"),

                    // 일본 (JP) - 단일 시간대
                    new LocaleDto("ja", "JP", "Asia/Tokyo"),

                    // 한국 (KR) - 단일 시간대
                    new LocaleDto("ko", "KR", "Asia/Seoul"),

                    // 독일 (DE) - 단일 시간대
                    new LocaleDto("de", "DE", "Europe/Berlin"),

                    // 프랑스 (FR) - 단일 시간대
                    new LocaleDto("fr", "FR", "Europe/Paris"),

                    // 인도 (IN) - 단일 시간대
                    new LocaleDto("hi", "IN", "Asia/Kolkata")
            );
        }

        return localeDtos;
    }

    @Getter
    @ToString
    public static class LocaleDto {
        private final String languageCode;
        private final String countryCode;
        private final String timeZone;
        private final String queryString;

        public LocaleDto(String languageCode, String countryCode, String timeZone) {
            this.languageCode = languageCode;
            this.countryCode = countryCode;
            this.timeZone = timeZone;
            this.queryString = String.format("locale=%s-%s&timezone=%s", languageCode, countryCode, timeZone);
        }
    }
}
