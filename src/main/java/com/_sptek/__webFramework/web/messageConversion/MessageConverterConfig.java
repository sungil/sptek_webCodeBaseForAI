package com._sptek.__webFramework.web.messageConversion;

import com._sptek.__webFramework.web.responseEscape.ResponseEscapingMappingJackson2HttpMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Spring MVC HTTP message converter 구성을 프레임워크 기본 ObjectMapper와 UTF-8 문자열 처리 기준으로 맞춘다.
 *
 * <p>JSON 응답은 {@link MappingJackson2HttpMessageConverter}가 담당하고,
 * text/html 및 text/plain 응답은 UTF-8 {@link StringHttpMessageConverter}가 담당한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class MessageConverterConfig implements WebMvcConfigurer {
    private final ObjectMapper objectMapper;
    private final MessageConversionProperties messageConversionProperties;

    /**
     * 프레임워크 ObjectMapper를 사용하는 JSON message converter를 생성한다.
     */
    //HTTP메시지(req, res) <-> object 변환 (MessageConverter 내부에서 ObjectMapper 사용)
    private ResponseEscapingMappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ResponseEscapingMappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new ResponseEscapingMappingJackson2HttpMessageConverter();
        configureJacksonConverter(mappingJackson2HttpMessageConverter);
        return mappingJackson2HttpMessageConverter;
    }

    /**
     * Boot 기본 converter 목록은 유지하고 JSON/String converter 정책만 보정한다.
     */
    @Override
    //framework에서 사용한 messageConvertor 설정
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        boolean hasJacksonConverter = false;
        boolean hasStringConverter = false;

        for (int i = 0; i < converters.size(); i++) {
            HttpMessageConverter<?> converter = converters.get(i);
            if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
                if (jacksonConverter instanceof ResponseEscapingMappingJackson2HttpMessageConverter responseEscapingJacksonConverter) {
                    configureJacksonConverter(responseEscapingJacksonConverter);
                } else {
                    ResponseEscapingMappingJackson2HttpMessageConverter responseEscapingJacksonConverter = mappingJackson2HttpMessageConverter();
                    converters.set(i, responseEscapingJacksonConverter);
                }
                hasJacksonConverter = true;
            }

            if (converter instanceof StringHttpMessageConverter stringConverter) {
                configureStringConverter(stringConverter);
                hasStringConverter = true;
            }
        }

        if (!hasJacksonConverter) {
            converters.add(0, mappingJackson2HttpMessageConverter());
        }

        if (!hasStringConverter) {
            StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(resolveStringCharset());
            configureStringConverter(stringHttpMessageConverter);
            converters.add(stringHttpMessageConverter);
        }
    }

    private void configureJacksonConverter(MappingJackson2HttpMessageConverter converter) {
        MessageConversionProperties.Json jsonProperties = messageConversionProperties.getJson();
        converter.setObjectMapper(objectMapper);
        converter.setSupportedMediaTypes(resolveMediaTypes(jsonProperties.getSupportedMediaTypes()));
        converter.setPrettyPrint(jsonProperties.isPrettyPrint());
        converter.setDefaultCharset(Charset.forName(jsonProperties.getDefaultCharset()));
    }

    private void configureStringConverter(StringHttpMessageConverter converter) {
        MessageConversionProperties.StringConverter stringProperties = messageConversionProperties.getString();
        converter.setDefaultCharset(resolveStringCharset());
        converter.setWriteAcceptCharset(stringProperties.isWriteAcceptCharset());
    }

    private Charset resolveStringCharset() {
        String charset = messageConversionProperties.getString().getDefaultCharset();
        if (charset == null || charset.isBlank()) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(charset);
    }

    private List<MediaType> resolveMediaTypes(List<String> mediaTypes) {
        if (mediaTypes == null || mediaTypes.isEmpty()) {
            return List.of(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        }
        return mediaTypes.stream()
                .map(MediaType::parseMediaType)
                .toList();
    }
}
