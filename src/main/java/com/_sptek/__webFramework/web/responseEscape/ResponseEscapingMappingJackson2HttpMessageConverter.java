package com._sptek.__webFramework.web.responseEscape;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;

/**
 * RestController 메서드 애노테이션에 따라 JSON 응답 직렬화 writer만 escape 모드로 교체한다.
 *
 * <p>컨트롤러 반환 객체를 직접 변경하지 않고, 실제 응답 JSON을 쓰는 시점에만 적용한다.</p>
 */
public class ResponseEscapingMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private static final JsonUnicodeEscapeCharacterEscapes JSON_UNICODE_CHARACTER_ESCAPES = new JsonUnicodeEscapeCharacterEscapes();

    public ResponseEscapingMappingJackson2HttpMessageConverter() {
    }

    public ResponseEscapingMappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable JavaType javaType, @Nullable MediaType contentType) {
        ResponseEscapeModeEnum responseEscapeMode = RestControllerMethodResponseEscapeModeResolver.resolve();

        if (responseEscapeMode == ResponseEscapeModeEnum.HTML_ENTITY) {
            return writer.withAttribute(ResponseEscapeModeEnum.WRITER_ATTRIBUTE_NAME, ResponseEscapeModeEnum.HTML_ENTITY);
        }

        if (responseEscapeMode == ResponseEscapeModeEnum.JSON_UNICODE) {
            return writer.with(JSON_UNICODE_CHARACTER_ESCAPES);
        }

        return writer;
    }
}
