package com._sptek.__webFramework.web.responseEscape;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

/**
 * JSON 응답 writer에 HTML entity escape 모드가 지정된 경우에만 String 값과 key를 escape 한다.
 */
public class JsonHtmlEntityEscapeModule extends SimpleModule {

    public JsonHtmlEntityEscapeModule() {
        addSerializer(String.class, new HtmlEntityEscapingStringSerializer());
        addKeySerializer(String.class, new HtmlEntityEscapingStringKeySerializer());
    }

    private static boolean isHtmlEntityEscapeMode(SerializerProvider serializerProvider) {
        return serializerProvider.getAttribute(ResponseEscapeModeEnum.WRITER_ATTRIBUTE_NAME) == ResponseEscapeModeEnum.HTML_ENTITY;
    }

    private static String escapeIfNeeded(String value, SerializerProvider serializerProvider) {
        if (!isHtmlEntityEscapeMode(serializerProvider)) {
            return value;
        }
        return escapeHtmlEntity(value);
    }

    private static String escapeHtmlEntity(String value) {
        StringBuilder escaped = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#39;");
                default -> escaped.append(ch);
            }
        }
        return escaped.toString();
    }

    private static class HtmlEntityEscapingStringSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(escapeIfNeeded(value, serializers));
        }
    }

    private static class HtmlEntityEscapingStringKeySerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeFieldName(escapeIfNeeded(value, serializers));
        }
    }
}
