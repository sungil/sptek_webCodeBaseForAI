package com.cesco.__projectsCommon.controllerAdvice;


import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ExampleResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 컨트롤러 클래스 타입 가져오기
        Class<?> containingClass = returnType.getContainingClass();
        boolean isController = containingClass.isAnnotationPresent(org.springframework.stereotype.Controller.class); //RestController는 제외됨
        //boolean hasCustomAnnotation = containingClass.isAnnotationPresent(XX.class); //구분을 위해 추가한 어노테이션

        // 두 조건이 모두 만족해야 동작
        return isController; // && hasCustomAnnotation;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // response 를 원하는 형태로 처리
        return body;
    }
}
