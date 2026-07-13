package com._sptek.__webFramework.web.locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.time.Duration;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Locale cookie, timezone cookie, 다국어 message source를 구성하는 MVC 설정.
 *
 * <p>locale 변경은 {@link CustomLocaleChangeInterceptor}가 query parameter와 cookie를 함께 처리한다.
 * message bundle은 cesco/__projectsCommon/i18n/messages 기준으로 로딩한다.</p>
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * locale 값을 cookie에 저장하는 LocaleResolver를 등록한다.
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver(LocaleConstants.LOCALE_COOKIE_NAME);
        // 서버 기준으로 디폴트
        cookieLocaleResolver.setDefaultLocale(Locale.getDefault());
        cookieLocaleResolver.setDefaultTimeZone(TimeZone.getDefault());
        cookieLocaleResolver.setCookieMaxAge(Duration.ofDays(LocaleConstants.LOCALE_COOKIE_MAX_AGE_DAY));
        cookieLocaleResolver.setCookieHttpOnly(true);
        return cookieLocaleResolver;
    }

    /**
     * locale query parameter 이름과 cookie 갱신 정책을 공유하는 locale 변경 interceptor를 등록한다.
     */
    @Bean
    public CustomLocaleChangeInterceptor localeChangeInterceptor() {
        CustomLocaleChangeInterceptor customLocaleChangeInterceptor = new CustomLocaleChangeInterceptor();
        //해당 이름으로 쿼리가 내려가면 해당 값으로 쿠키가 내려가며 동시에 locale 값으로 세팅됨
        customLocaleChangeInterceptor.setParamName(LocaleConstants.LOCALE_COOKIE_NAME);
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
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath:/cesco/__projectsCommon/i18n/messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        reloadableResourceBundleMessageSource.setCacheSeconds(60*10); // 리로드 시간
        return reloadableResourceBundleMessageSource;
    }
}
