package com._sptek.__webFramework.web.xss;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_At_Bean(value = Enable_XssProtectForApi_At_Main.class, negate = true) //Enable_XssProtectForApi_At_Main 이 적용되는 경우 ObjectMapper 에서 XssProtectHelper 를 통해 일괄 처리됨
@ControllerAdvice

public class ControllerAdviceForApiXssProtect implements ResponseBodyAdvice<Object>  {
    private final XssEscapeSupport xssEscapeSupport;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(Enable_XssProtectForApi_At_ControllerMethod.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 이미 ResponseEntity 객체일 경우
        if (body instanceof ResponseEntity<?> responseEntity) {
            // ResponseEntity의 body만 가공 후 새로 생성해 반환
            Object escapedBody = xssEscapeSupport.escape(responseEntity.getBody());
            return ResponseEntity.status(responseEntity.getStatusCode())
                    .headers(responseEntity.getHeaders())
                    .body(escapedBody);
        } else {
            return xssEscapeSupport.escape(body);
        }
    }
}
