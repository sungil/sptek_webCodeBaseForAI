package com._sptek.__webFramework.web.xss;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonUnicodeEscapeCharacterEscapesTest {

    @Test
    void unicodeEscapeChangesJsonTextButKeepsParsedValue() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String origin = "</script><script>alert('x')&</script>";

        String json = objectMapper.writer()
                .with(new JsonUnicodeEscapeCharacterEscapes())
                .writeValueAsString(Map.of("value", origin));

        assertThat(json).contains("\\u003C/script\\u003E");
        assertThat(json).contains("\\u0027x\\u0027");
        assertThat(json).contains("\\u0026");

        Map<String, String> parsed = objectMapper.readValue(json, new TypeReference<>() {});
        assertThat(parsed.get("value")).isEqualTo(origin);
    }
}
