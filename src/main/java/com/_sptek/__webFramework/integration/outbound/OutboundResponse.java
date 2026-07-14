package com._sptek.__webFramework.integration.outbound;

import org.springframework.http.HttpHeaders;

/**
 * {@link OutboundSupport}가 외부 HTTP 호출 결과로 반환하는 응답 모델.
 */
public record OutboundResponse(int code, HttpHeaders headers, String body) {}
