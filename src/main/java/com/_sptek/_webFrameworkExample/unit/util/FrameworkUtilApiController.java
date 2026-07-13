package com._sptek._webFrameworkExample.unit.util;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.web.util.RequestUtil;
import com._sptek.__webFramework.core.util.TypeConvertUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Utils", description = "")

public class FrameworkUtilApiController {
    @GetMapping("/01/example/util/testRequestUtil")
    @Operation(summary = "01. request 요청과 관련해 URL, reqIP, Header, parameter를 제공", description = "")
    public Object reqResUtil(HttpServletRequest request) {
        String reqFullUrl = RequestUtil.getRequestUrlQuery(request);
        String reqIp = RequestUtil.getReqUserIp(request);
        String headers = RequestUtil.getRequestHeaderMap(request).toString();
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(request));

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("reqFullUrl", reqFullUrl);
        resultMap.put("reqIp", reqIp);
        resultMap.put("headers", headers);
        resultMap.put("params", params);

        return resultMap;
    }
}
