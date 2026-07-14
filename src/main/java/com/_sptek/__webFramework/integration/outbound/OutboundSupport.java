package com._sptek.__webFramework.integration.outbound;

import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import com._sptek.__webFramework.core.util.SpringUtil;
import com._sptek.__webFramework.core.util.TypeConvertUtil;
import com._sptek.__webFramework.observability.logging.Enable_OutboundSupportDetailLog_At_Main;
import com._sptek.__webFramework.observability.logging.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import com._sptek.__webFramework.web.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Apache {@link CloseableHttpClient} 기반 외부 HTTP 호출을 프레임워크 표준 응답 모델과 로깅 규약으로 감싼 지원 클래스.
 *
 * <p>요청 헤더와 바디를 공통 유틸로 적용하고, 응답 코드는 {@link OutboundResponse}로 반환한다.
 * 메인 클래스 어노테이션 설정에 따라 outbound 상세 로그 및 현재 요청의 Req/Res 상세 로그 연계 정보도 남긴다.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class OutboundSupport {
    private final CloseableHttpClient closeableHttpClient;
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;

    public OutboundResponse request(HttpMethod httpMethod, UriComponents uriComponents) throws Exception {return request(httpMethod, uriComponents, null, null);}
    public OutboundResponse request(HttpMethod httpMethod, UriComponents uriComponents, HttpHeaders httpHeaders) throws Exception {return request(httpMethod, uriComponents, httpHeaders, null);}
    public OutboundResponse request(HttpMethod httpMethod, UriComponents uriComponents, Object requestBody) throws Exception {return request(httpMethod, uriComponents, null, requestBody);}

    /**
     * HTTP method, URI, 선택 헤더, 선택 바디를 받아 외부 API를 호출하고 응답 코드/헤더/바디를 반환한다.
     */
    public OutboundResponse request(HttpMethod httpMethod, UriComponents uriComponents, @Nullable HttpHeaders httpHeaders, @Nullable Object requestBody) throws Exception {
        URI uri = uriComponents.encode().toUri();
        HttpUriRequest request = switch (httpMethod.toString()) {
            case "GET"    -> new HttpGet(uri);
            case "POST"   -> new HttpPost(uri);
            case "PUT"    -> new HttpPut(uri);
            case "DELETE" -> new HttpDelete(uri);
            default -> throw new IllegalArgumentException("Unsupported method: " + httpMethod);
        };

        if (httpHeaders == null) httpHeaders = new HttpHeaders();
        if (!StringUtils.hasText(httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE))) httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        RequestUtil.applyRequestHeaders(request, httpHeaders);
        RequestUtil.applyRequestBody(request, requestBody);

        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(request)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            Arrays.stream(closeableHttpResponse.getHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));

            String outboundId = LocalDateTime.now().toString();
            String responseBodyStr = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
            OutboundResponse outboundResponse = new OutboundResponse(closeableHttpResponse.getCode(), responseHeaders, responseBodyStr);

            justLogging(requestBody, outboundId, httpMethod, uriComponents, httpHeaders, outboundResponse);
            return outboundResponse;
        }
    }

    /**
     * 메인 설정과 현재 요청 컨텍스트를 기준으로 outbound 상세 로그와 Req/Res 연계 로그를 기록한다.
     */
    private void justLogging(Object requestBody, String outboundId, HttpMethod httpMethod, UriComponents uriComponents, HttpHeaders httpHeaders
            , OutboundResponse outboundResponse) throws Exception {

        if (MainClassAnnotationRegister.hasAnnotation(Enable_OutboundSupportDetailLog_At_Main.class)) {
            String requestBodyStr = "";
            if (requestBody != null) {
                requestBodyStr = requestBody instanceof String ? requestBody.toString() : TypeConvertUtil.objectToJsonWithoutRootName(requestBody, true);
                requestBodyStr = StringUtils.hasText(requestBodyStr) ? "\n" + requestBodyStr : "";
            }
            String logContent = """
                    outBoundId : %s
                    -->(%s) url : %s
                    requestHeader : %s
                    requestBody : %s
                    <--(%s)
                    responseHeader : %s
                    responseBody : %s
                    """.formatted(outboundId, httpMethod.name(), uriComponents.toString(), httpHeaders.toString(), requestBodyStr
                            , outboundResponse.code(), outboundResponse.headers().toString(), outboundResponse.body());
            String logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_OutboundSupportDetailLog_At_Main.class).get("value"), "");
            log.info(LoggingUtil.makeBaseForm(logTag, "Outbound Support Detail Log", logContent));
        }

        HttpServletRequest currentRequest = SpringUtil.getRequestOrNull();
        if (currentRequest != null) {
            boolean hasReqResDetailLog = MainClassAnnotationRegister.hasAnnotation(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                    || Boolean.TRUE.equals(currentRequest.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED))
                    || requestMappingAnnotationRegister.hasAnnotation(currentRequest, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class);

            if (hasReqResDetailLog) {
                List<String> relatedOutbounds = (List<String>) currentRequest.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS);
                if (relatedOutbounds == null) {
                    relatedOutbounds = new ArrayList<>();
                }
                relatedOutbounds.add(outboundId + " " + httpMethod.name() + " " + uriComponents.toString() + " --> " + outboundResponse.code());
                currentRequest.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS, relatedOutbounds);
            }
        }
    }

    /**
     * @deprecated {@link OutboundResponse#body()} 사용 흐름으로 대체되었으며 기존 HttpEntity 처리 호환용으로만 남겨둔다.
     */
    @Deprecated
    public static String DEPRECATED_convertResponseToString(HttpEntity httpEntity) throws Exception {
        String reponseString =EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        log.debug("responseBody to String = {}", reponseString);

        EntityUtils.consume(httpEntity);
        return reponseString;
    }
}
