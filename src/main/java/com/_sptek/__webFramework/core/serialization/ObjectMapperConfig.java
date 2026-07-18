package com._sptek.__webFramework.core.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com._sptek.__webFramework.web.responseEscape.JsonHtmlEntityEscapeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * API JSON 직렬화/역직렬화에 사용할 ObjectMapper Bean을 구성한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ObjectMapperConfig {
    private final ObjectMapperProperties objectMapperProperties;

    //jason->object, object->jason
    //Locale 과 TimeZone 은 system default로 설정함 (user 별 변환이 필요시 해당 user의 locale 을 이용해서 변환된 string 값을 내리도록 할것)

    @Bean
    public ObjectMapper objectMapper() {
        //locale, timeZone등 공통요소에 대한 setting을 할수 있다.
        return configureBaseObjectMapper(new ObjectMapper());
    }

    private ObjectMapper configureBaseObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JsonHtmlEntityEscapeModule());
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
