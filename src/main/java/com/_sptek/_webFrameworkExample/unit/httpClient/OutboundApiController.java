package com._sptek._webFrameworkExample.unit.httpClient;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.integration.outbound.OutboundResponse;
import com._sptek.__webFramework.integration.outbound.OutboundSupport;
import com._sptek.__webFramework.core.util.TypeConvertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Outbound (Pooling connection Manager)", description = "")

public class OutboundApiController {
    private final OutboundSupport outboundSupport;
    String apiTestUrl = "https://jsonplaceholder.typicode.com";
    OutboundDto outboundDto = new OutboundDto(0, "my title!", "my content!", null);

    @GetMapping("/01/example/outbound/closeableHttpClientGet")
    @Operation(summary = "01. closeableHttpClient Get without Pool", description = "")
    public Object closeableHttpClientGet() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(apiTestUrl)
                .pathSegment("posts", "{id}")
                .queryParam("myKey", "myValue")
                .buildAndExpand(Map.of("id", 1));

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(uriComponents.encode().toUri());
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                return response.getCode();
            }
        }
    }

    @GetMapping("/02/example/outbound/closeableHttpClientPost")
    @Operation(summary = "02. closeableHttpClient Post without Pool", description = "")
    public Object closeableHttpClientPost() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(apiTestUrl)
                .pathSegment("posts")
                .queryParam("myKey", "myValue")
                .build();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(uriComponents.encode().toUri());
            request.addHeader("X-TEST_KEY", "X-TEST_VALUE");
            request.setEntity(new StringEntity(
                    // object -> json
                    TypeConvertUtil.objectToJsonWithoutRootName(outboundDto, false)
                    , ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse closeableHttpResponse = httpClient.execute(request)) {
                if (closeableHttpResponse.getCode() < 200 || closeableHttpResponse.getCode() >= 300) return "http client request failed. (result code: " + closeableHttpResponse.getCode() + ")";
                return TypeConvertUtil.jsonToClass(EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8), OutboundDto.class);
            }
        }
    }

    @GetMapping("/03/example/outbound/outboundSupportGet")
    @Operation(summary = "03. OutboundSupport Get with Pool", description = "")
    public Object outboundSupportGet() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(apiTestUrl)
                .pathSegment("posts", "{id}")
                .queryParam("myKey", "myValue")
                .buildAndExpand(Map.of("id", 1));

        OutboundResponse outboundResponse = outboundSupport.request(HttpMethod.GET, uriComponents);
        return outboundResponse.code();
    }

    @GetMapping("/04/example/outbound/outboundSupportPost")
    @Operation(summary = "04. OutboundSupport POST with Pool", description = "")
    public Object outboundSupportPost() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(apiTestUrl)
                .pathSegment("posts")
                .queryParam("myKey", "myValue")
                .build();

        HttpHeaders httpHeaders = TypeConvertUtil.objMapToHttpHeaders(Map.of("X-TEST_KEY", "X-TEST_VALUE"));
        OutboundResponse outboundResponse = outboundSupport.request(HttpMethod.POST, uriComponents, httpHeaders, outboundDto);
        if (outboundResponse.code() < 200 || outboundResponse.code() >= 300)  return "http client request failed. (result code: " + outboundResponse.code() + ")";
        return TypeConvertUtil.jsonToClass(outboundResponse.body(), OutboundDto.class);
    }

    @GetMapping("/05/example/outbound/outboundSupportPut")
    @Operation(summary = "05. OutboundSupport PUT with Pool", description = "")
    public Object outboundSupportPut() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(apiTestUrl)
                .pathSegment("posts", "{id}")
                .queryParam("myKey", "myValue")
                .buildAndExpand(Map.of("id", 1));

        HttpHeaders httpHeaders = TypeConvertUtil.objMapToHttpHeaders(Map.of("X-TEST_KEY", "X-TEST_VALUE"));
        OutboundResponse outboundResponse = outboundSupport.request(HttpMethod.PUT, uriComponents, httpHeaders, outboundDto);
        if (outboundResponse.code() < 200 || outboundResponse.code() >= 300) return "http client request failed. (result code: " + outboundResponse.code() + ")";
        return TypeConvertUtil.jsonToClass(outboundResponse.body(), OutboundDto.class);
    }

    @GetMapping("/06/example/outbound/outboundSupportDelete")
    @Operation(summary = "06. OutboundSupport DELETE with Pool", description = "")
    public Object outboundSupportDelete() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder
                .fromHttpUrl(apiTestUrl)
                .pathSegment("posts", "{id}")
                .queryParam("myKey", "myValue")
                .buildAndExpand(Map.of("id", 1));

        HttpHeaders httpHeaders = TypeConvertUtil.objMapToHttpHeaders(Map.of("X-TEST_KEY", "X-TEST_VALUE"));
        OutboundResponse outboundResponse = outboundSupport.request(HttpMethod.DELETE, uriComponents, httpHeaders);
        if (outboundResponse.code() < 200 || outboundResponse.code() >= 300) return "http client request failed. (result code: " + outboundResponse.code() + ")";
        return TypeConvertUtil.jsonToClass(outboundResponse.body(), OutboundDto.class);
    }

    // api Test Dto
    private record OutboundDto(int id, String title, String content, String extraField) {}
}


//---> 별도 스레드로 여러개 강제로 돌려서 모니터링 해볼것
