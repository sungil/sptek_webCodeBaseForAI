package com._sptek.__webFramework.integration.outbound;

import com._sptek.__webFramework.core.util.TypeConvertUtil;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * Apache HttpClient 기반 outbound 요청 객체에 프레임워크 표준 헤더와 바디를 적용하는 유틸리티.
 */
public class OutboundRequestUtil {

    /**
     * Spring HttpHeaders 값을 Apache HttpClient 요청 객체에 추가한다.
     */
    public static void applyRequestHeaders(HttpUriRequest httpUriRequest, @Nullable HttpHeaders httpHeaders) {
        if (httpHeaders == null || httpHeaders.isEmpty()) return;
        httpHeaders.forEach((name, values) ->
                values.forEach(value -> httpUriRequest.addHeader(name, value))
        );
    }

    /**
     * 문자열 또는 객체 request body를 JSON 문자열로 변환해 Apache HttpClient 요청 entity에 설정한다.
     */
    public static void applyRequestBody(HttpUriRequest httpUriRequest, @Nullable Object requestBody) throws Exception {
        if (requestBody == null) return;
        String requestBodyString = requestBody instanceof String ? requestBody.toString() : TypeConvertUtil.objectToJsonWithoutRootName(requestBody, false);
        if (StringUtils.hasText(requestBodyString)) httpUriRequest.setEntity(new StringEntity(requestBodyString, StandardCharsets.UTF_8));
    }
}
