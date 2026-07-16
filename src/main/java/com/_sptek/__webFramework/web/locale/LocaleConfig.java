package com._sptek.__webFramework.web.locale;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.time.Duration;
import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Locale cookie, timezone cookie, 다국어 message source를 구성하는 MVC 설정.
 *
 * <p>locale 변경은 {@link CustomLocaleChangeInterceptor}가 query parameter와 cookie를 함께 처리한다.
 * message bundle 경로와 cookie 정책은 {@link LocaleProperties}를 기준으로 한다.</p>
 */
@Configuration
@RequiredArgsConstructor
@HasAnnotationOnMain_At_Bean(Enable_LocaleSupport_At_Main.class)
public class LocaleConfig implements WebMvcConfigurer {
    private final LocaleProperties localeProperties;

    /**
     * locale 값을 cookie에 저장하는 LocaleResolver를 등록한다.
     */
    @Bean
    public LocaleResolver localeResolver() {
        LocaleProperties.Cookie cookie = localeProperties.getCookie();
        LocaleProperties.DefaultValue defaultValue = localeProperties.getDefaultValue();
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver(cookie.getLocaleName());
        // 서버 기준으로 디폴트
        cookieLocaleResolver.setDefaultLocale(Locale.forLanguageTag(defaultValue.getLocale()));
        cookieLocaleResolver.setDefaultTimeZone(TimeZone.getTimeZone(ZoneId.of(defaultValue.getTimezone())));
        cookieLocaleResolver.setCookieMaxAge(Duration.ofDays(cookie.getMaxAgeDays()));
        cookieLocaleResolver.setCookieHttpOnly(cookie.isHttpOnly());
        cookieLocaleResolver.setCookieSecure(cookie.isSecure());
        cookieLocaleResolver.setCookieSameSite(cookie.getSameSite());
        return cookieLocaleResolver;
    }

    /**
     * locale query parameter 이름과 cookie 갱신 정책을 공유하는 locale 변경 interceptor를 등록한다.
     */
    @Bean
    public CustomLocaleChangeInterceptor localeChangeInterceptor() {
        CustomLocaleChangeInterceptor customLocaleChangeInterceptor = new CustomLocaleChangeInterceptor(localeProperties);
        //해당 이름으로 쿼리가 내려가면 해당 값으로 쿠키가 내려가며 동시에 locale 값으로 세팅됨
        customLocaleChangeInterceptor.setParamName(localeProperties.getCookie().getLocaleName());
        return customLocaleChangeInterceptor;
    }

    /**
     * 모든 MVC 요청에 locale/timezone 변경 interceptor를 적용한다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * 프로젝트 공통 i18n message bundle을 UTF-8로 읽는 MessageSource를 등록한다.
     */
    @Bean
    public MessageSource messageSource() {
        LocaleProperties.MessageSource messageSource = localeProperties.getMessageSource();
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename(messageSource.getBasename());
        reloadableResourceBundleMessageSource.setDefaultEncoding(messageSource.getEncoding());
        reloadableResourceBundleMessageSource.setCacheSeconds(messageSource.getCacheSeconds()); // 리로드 시간
        return reloadableResourceBundleMessageSource;
    }
}
