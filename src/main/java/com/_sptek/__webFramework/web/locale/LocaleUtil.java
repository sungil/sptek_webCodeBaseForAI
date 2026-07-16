package com._sptek.__webFramework.web.locale;

import com._sptek.__webFramework.web.util.CookieUtil;
import com._sptek.__webFramework.core.util.SpringUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
@RequiredArgsConstructor
/**
 * 현재 요청/쿠키/기본 Locale을 기준으로 다국어 메시지와 사용자 Locale 정보를 제공하는 유틸리티.
 */
public class LocaleUtil {
    private static final MessageSource messageSource;

    //static final 상수 초기화에 사용, 생성자 보다 먼저 실행됨
    static {
        messageSource = SpringUtil.getSpringBean(MessageSource.class);
    }

    /**
     * 현재 Locale에 맞는 메시지를 인자 없이 조회한다.
     */
    public static String getI18nMessage(String code) {
        return LocaleUtil.getI18nMessage(code, null);
    }

    /**
     * 단일 인자를 포함해 현재 Locale에 맞는 메시지를 조회한다.
     */
    public static String getI18nMessage(String code, @Nullable Object arg) {
        if (arg == null) {
            return getI18nMessage(code, null);
        } else {
            return LocaleUtil.getI18nMessage(code, new Object[]{arg});
        }
    }

    /**
     * 현재 Locale에 해당하는 메시지를 조회한다.
     *
     * <p>요청의 LocaleResolver가 없으면 locale 쿠키, JVM 기본 언어 순서로 fallback 한다.</p>
     */
    public static String getI18nMessage(String code, @Nullable Object[] args) {
        String langCode;
        HttpServletRequest request = SpringUtil.getRequestOrNull();
        if (request != null && RequestContextUtils.getLocaleResolver(request) != null) {
            langCode = LocaleContextHolder.getLocale().toLanguageTag();

        } else {
            // 로그아웃 등으로 세션이 종료되어 localeResolver 가 삭제 되더라도 locale 쿠키가 남아 있다면 locale 쿠키로 다국어 지원을 하기 위해서
            // 사실 CustomLocaleChangeInterceptor 가 등록되어 있는 경우 이 케이스는 나오지 않을 것임
            List<Cookie> localeCookie = CookieUtil.getCookies(localeProperties().getCookie().getLocaleName());
            langCode = !localeCookie.isEmpty() ? localeCookie.get(0).getValue() : Locale.getDefault().getLanguage();
        }

        return messageSource.getMessage(code, args, Locale.forLanguageTag(langCode));
    }

    private static LocaleProperties localeProperties() {
        return SpringUtil.getSpringBean(LocaleProperties.class);
    }

    /**
     * 현재 사용자 요청에 바인딩된 Locale을 반환한다.
     */
    public static Locale getCurUserLocale(){
        return LocaleContextHolder.getLocale();
    }

    /**
     * 현재 사용자 요청에 바인딩된 TimeZone을 반환한다.
     */
    public static TimeZone getCurUserTimeZone(){
        return LocaleContextHolder.getTimeZone();
    }

    /**
     * 현재 사용자 Locale을 BCP 47 language tag 문자열로 반환한다.
     */
    public static String getCurUserLanguageTag(){
        return LocaleContextHolder.getLocale().toLanguageTag();
    }

    /**
     * 현재 사용자 TimeZone 표시명을 반환한다.
     */
    public static String getCurUserTimeZoneName(){
        return LocaleContextHolder.getTimeZone().getDisplayName();
    }

    /**
     * 화면 선택 등에 사용할 지원 국가/언어/시간대 조합을 properties에서 조회한다.
     */
    public static List<LocaleProperties.SupportedLocale> getSupportedLocales() {
        return localeProperties().getSupportedLocales();
    }

    /**
     * 화면 선택 등에 사용할 주요 국가/언어/시간대 조합을 반환한다.
     *
     * @deprecated 주요 Locale 목록은 프로젝트 설정값이므로 {@link #getSupportedLocales()}를 사용한다.
     */
    @Deprecated
    public static List<LocaleProperties.SupportedLocale> getMajorLocales() {
        return getSupportedLocales();
    }
}
