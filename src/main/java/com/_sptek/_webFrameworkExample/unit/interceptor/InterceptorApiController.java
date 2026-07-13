package com._sptek._webFrameworkExample.unit.interceptor;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Interceptor", description = "")

public class InterceptorApiController {

    // 프로젝트 쪽의 ExampleInterceptor 로 예시 함
    @GetMapping("/01/projectName/interceptor/interceptorGuide")
    @Operation(summary = "01. Interceptor 구성을 위한 기본 가이드 ", description = "")
    public Object interceptorGuide() {
        log.debug("---> 2. Inside of Controller");
        return "Log 와 ExampleInterceptor class 를 참고 하세요.";
    }

}
