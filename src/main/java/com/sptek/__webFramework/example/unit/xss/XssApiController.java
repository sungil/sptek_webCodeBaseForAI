package com.sptek.__webFramework.example.unit.xss;

import com.sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com.sptek.__webFramework.web.xss.Enable_XssProtectForApi_At_ControllerMethod;
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
    @Operation(summary = "01. XssProtect 어노테이션 미적용", description = "")
    public Object xssProtectOff(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody) {
        //@Enable_XssProtectForApi_At_Main 가 적용된 경우 @Enable_XssProtectForApi_At_ControllerMethod 가 없어도 일괄 XssProtect 적용됨
        return originBody;
    }

    @PostMapping("/02/example/xss/xssProtectOn")
    @Enable_XssProtectForApi_At_ControllerMethod
    @Operation(summary = "02. XssProtect 어노테이션 적용 (응답 결과를 Escape 처리함)", description = "")
    public Object xssProtectOn(@Parameter(name = "originBody", description = "스크립트 요소를 포함한 요청 body") @RequestBody String originBody){
        // 컨트롤러에 전달되는 값은 원본값 그데로임(json으로 변환되어 나깔때 HTML Entity 코드로 변함됨)
        return originBody;
    }
}
