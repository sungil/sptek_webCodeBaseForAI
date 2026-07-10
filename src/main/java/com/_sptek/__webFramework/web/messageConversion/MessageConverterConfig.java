package com._sptek.__webFramework.web.messageConversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.*;

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

    /**
     * 프레임워크 ObjectMapper를 사용하는 JSON message converter를 등록한다.
     */
    @Bean
    //HTTP메시지(req, res) <-> object 변환 (MessageConverter 내부에서 ObjectMapper 사용)
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(APPLICATION_JSON); //JSON 응답때 적용됨

        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        mappingJackson2HttpMessageConverter.setPrettyPrint(true);
        mappingJackson2HttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        return mappingJackson2HttpMessageConverter;
    }

    /**
     * MVC message converter 목록을 JSON converter와 UTF-8 String converter 기준으로 구성한다.
     */
    @Override
    //framework에서 사용한 messageConvertor 설정
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //json 을 위한 매시지 컨버터로 등록
        converters.add(mappingJackson2HttpMessageConverter());

        //그외의 경우 기본용
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(TEXT_HTML); //TEXT_HTML 또는 TEXT_PLAIN 때 적용됨
        supportedMediaTypes.add(TEXT_PLAIN);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        stringHttpMessageConverter.setDefaultCharset(StandardCharsets.UTF_8);
        converters.add(stringHttpMessageConverter);

        WebMvcConfigurer.super.configureMessageConverters(converters);
    }
}
