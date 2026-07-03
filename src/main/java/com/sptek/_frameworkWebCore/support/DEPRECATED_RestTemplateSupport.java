package com.sptek._frameworkWebCore.support;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;

/**
 * @deprecated Apache HttpClient 기반 {@link OutboundSupport}로 대체된 RestTemplate 호출 지원 클래스.
 *
 * <p>기존 코드 호환을 위해 유지하며, 신규 외부 호출 구현에는 사용하지 않는다.</p>
 */
@Deprecated
@Slf4j
@RequiredArgsConstructor
public class DEPRECATED_RestTemplateSupport {

    private final RestTemplate restTemplate;

    /**
     * 기존 RestTemplate 기반 GET 호출을 수행한다.
     */
    public ResponseEntity<String> requestGet(String requestUri, @Nullable LinkedMultiValueMap<String, String> queryParams, @Nullable HttpHeaders httpHeaders) {
        log.debug("requestUri = ({}), queryParams = ({}), httpHeaders = ({})", requestUri, queryParams, httpHeaders);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUri);
        if (queryParams != null) {
            builder.queryParams(queryParams);
        }
        String finalUrl = builder.toUriString();

        RequestEntity<Void> requestEntity = RequestEntity
                .method(HttpMethod.GET, finalUrl)
                .headers(httpHeaders != null ? httpHeaders : new HttpHeaders())
                .build();
        return restTemplate.exchange(requestEntity, String.class);
    }

    /**
     * 기존 RestTemplate 기반 POST 호출을 수행한다.
     */
    public ResponseEntity<String> requestPost(String requestUri, @Nullable LinkedMultiValueMap<String, String> queryParams, @Nullable HttpHeaders httpHeaders, @Nullable Object requestBody) {
        log.debug("requestUri = ({}), queryParams = ({}), httpHeaders = ({}), requestBody = ({})", requestUri, queryParams, httpHeaders, requestBody);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUri);
        if (queryParams != null) {
            builder.queryParams(queryParams);
        }
        String finalUrl = builder.toUriString();
        HttpHeaders headers = httpHeaders != null ? new HttpHeaders(httpHeaders) : new HttpHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            log.warn("Content-Type header is missing. Setting default to APPLICATION_JSON");
            headers.setContentType(MediaType.APPLICATION_JSON);
        }

        RequestEntity<Object> requestEntity = RequestEntity
                .post(finalUrl)
                .headers(httpHeaders)
                .body(requestBody != null ? requestBody : new HashMap<>());
        return restTemplate.exchange(requestEntity, String.class);
    }

    /**
     * RestTemplate 문자열 응답 본문을 꺼내 로그로 남긴다.
     */
    public String convertResponseToString(ResponseEntity<String> responseEntity) {
        String responseString = responseEntity.getBody();
        log.debug("responseBody to String = {}", responseString);
        return responseString;
    }
}
