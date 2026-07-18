package com._sptek._webFrameworkExample.unit.xss;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.web.responseEscape.Enable_HtmlEntityEscapeForJsonResponse_At_RestControllerMethod;
import com._sptek.__webFramework.web.responseEscape.Enable_UnicodeEscapeForJsonResponse_At_RestControllerMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Xss", description = "")

public class XssApiController {

    @PostMapping("/01/example/xss/xssProtectOff")
    @Operation(summary = "01. 응답 escape 어노테이션 미적용", description = "")
    public Object responseEscapeOff(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody) {
        return originBody;
    }

    @PostMapping("/02/example/xss/xssProtectOn")
    @Enable_HtmlEntityEscapeForJsonResponse_At_RestControllerMethod
    @Operation(summary = "02. HTML entity 응답 escape 적용", description = "")
    public Object htmlEntityEscapeOn(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody){
        // HTML entity 방식은 JSON 파싱 후 값도 &lt; 같은 표시용 문자열로 바뀐다.
        return originBody;
    }

    @PostMapping("/03/example/xss/unicodeEscapeOn")
    @Enable_UnicodeEscapeForJsonResponse_At_RestControllerMethod
    @Operation(summary = "03. JSON Unicode 응답 escape 적용", description = "")
    public Object unicodeEscapeOn(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody){
        // Unicode 방식은 JSON 문서 표현만 바꾸며, JSON 파싱 후 값은 원문으로 복원된다.
        return originBody;
    }
}
