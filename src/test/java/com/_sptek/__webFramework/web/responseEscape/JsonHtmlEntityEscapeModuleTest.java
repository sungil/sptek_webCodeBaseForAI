package com._sptek.__webFramework.web.responseEscape;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JsonHtmlEntityEscapeModuleTest {

    @Test
    void htmlEntityEscapeRunsOnlyWhenWriterAttributeIsEnabled() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JsonHtmlEntityEscapeModule());

        String origin = "<script>alert('x') & \"q\"</script>";
        String defaultJson = objectMapper.writeValueAsString(Map.of("value", origin));
        String escapedJson = objectMapper.writer()
                .withAttribute(ResponseEscapeModeEnum.WRITER_ATTRIBUTE_NAME, ResponseEscapeModeEnum.HTML_ENTITY)
                .writeValueAsString(Map.of("value", origin));

        assertThat(defaultJson).contains("<script>");
        assertThat(escapedJson).contains("&lt;script&gt;");
        assertThat(escapedJson).contains("&#39;x&#39;");
        assertThat(escapedJson).contains("&amp;");
        assertThat(escapedJson).contains("&quot;q&quot;");
    }
}
