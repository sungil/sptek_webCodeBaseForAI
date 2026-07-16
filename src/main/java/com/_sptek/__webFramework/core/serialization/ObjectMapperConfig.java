package com._sptek.__webFramework.core.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com._sptek.__webFramework.web.xss.Enable_XssProtectForApi_At_Main;
import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com._sptek.__webFramework.web.xss.XssProtectHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * API JSON 직렬화/역직렬화에 사용할 ObjectMapper Bean을 구성한다.
 *
 * <p>메인 클래스의 {@link Enable_XssProtectForApi_At_Main} 적용 여부에 따라
 * 전역 XSS character escape 적용 ObjectMapper와 일반 ObjectMapper 중 하나를 등록한다.</p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfig {
    private final ObjectMapperProperties objectMapperProperties;

    //jason->object, object->jason
    //Locale 과 TimeZone 은 system default로 설정함 (user 별 변환이 필요시 해당 user의 locale 을 이용해서 변환된 string 값을 내리도록 할것)

    /**
     * API XSS 전역 escape가 비활성화된 경우 사용하는 기본 ObjectMapper를 등록한다.
     */
    @Bean
    // @Enable_XssProtectForApi_At_ControllerMethod를 통해 선별적 xss 처리, 더 권장
    @HasAnnotationOnMain_At_Bean(value = Enable_XssProtectForApi_At_Main.class, negate = true)
    public ObjectMapper objectMapperWithoutXssProtectHelper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        return configureBaseObjectMapper(new ObjectMapper());
    }

    /**
     * API XSS 전역 escape가 활성화된 경우 CharacterEscapes가 적용된 ObjectMapper를 등록한다.
     */
    @Bean
    @HasAnnotationOnMain_At_Bean(value = Enable_XssProtectForApi_At_Main.class, negate = false)
    public ObjectMapper objectMapperWithXssProtectHelper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        ObjectMapper objectMapper = configureBaseObjectMapper(new ObjectMapper());
        objectMapper.getFactory().setCharacterEscapes(new XssProtectHelper()); //Xss 방지 적용
        return objectMapper;
    }

    private ObjectMapper configureBaseObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setLocale(resolveLocale());
        objectMapper.setTimeZone(resolveTimeZone());
        objectMapper.setSerializationInclusion(objectMapperProperties.isIncludeNonNullOnly()
                ? JsonInclude.Include.NON_NULL
                : JsonInclude.Include.ALWAYS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, objectMapperProperties.isFailOnUnknownProperties());
        return objectMapper;
    }

    private Locale resolveLocale() {
        String defaultLocale = objectMapperProperties.getDefaultLocale();
        if (defaultLocale == null || defaultLocale.isBlank() || "system".equalsIgnoreCase(defaultLocale)) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(defaultLocale);
    }

    private TimeZone resolveTimeZone() {
        String defaultTimezone = objectMapperProperties.getDefaultTimezone();
        if (defaultTimezone == null || defaultTimezone.isBlank() || "system".equalsIgnoreCase(defaultTimezone)) {
            return TimeZone.getDefault();
        }
        return TimeZone.getTimeZone(ZoneId.of(defaultTimezone));
    }
}
