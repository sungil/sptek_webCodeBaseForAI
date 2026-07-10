package com._sptek.__webFramework.integration.httpClient;

import org.springframework.http.HttpHeaders;

//record 를 통해 생성자, getter 를 자동 생성함
public record HttpClientResponseDto (int code, HttpHeaders headers, String body) {}
