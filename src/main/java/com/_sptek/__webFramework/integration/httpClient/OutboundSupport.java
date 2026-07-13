package com._sptek.__webFramework.integration.httpClient;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.observability.logging.Enable_OutboundSupportDetailLog_At_Main;
import com._sptek.__webFramework.observability.logging.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import com._sptek.__webFramework.web.util.RequestUtil;
import com._sptek.__webFramework.core.util.SpringUtil;
import com._sptek.__webFramework.core.util.TypeConvertUtil;
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
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Apache {@link CloseableHttpClient} 기반 외부 HTTP 호출을 프레임워크 표준 DTO와 로깅 규약으로 감싼 지원 클래스.
 *
 * <p>요청 헤더와 바디를 공통 유틸로 적용하고, 응답 코드는 {@link HttpClientResponseDto}로 반환한다.
 * 메인 클래스 어노테이션 설정에 따라 outbound 상세 로그 및 현재 요청의 Req/Res 상세 로그 연계 정보도 남긴다.</p>
 */

@Slf4j
@RequiredArgsConstructor
public class OutboundSupport {
    private final CloseableHttpClient closeableHttpClient;
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;

    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents) throws Exception {return request(httpMethod, uriComponents, null, null);}
    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents, HttpHeaders httpHeaders) throws Exception {return request(httpMethod, uriComponents, httpHeaders, null);}
    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents, Object requestBody) throws Exception {return request(httpMethod, uriComponents, null, requestBody);}

    /**
     * HTTP method, URI, 선택 헤더, 선택 바디를 받아 외부 API를 호출하고 응답 코드/헤더/바디를 반환한다.
     */
    public HttpClientResponseDto request(HttpMethod httpMethod, UriComponents uriComponents, @Nullable HttpHeaders httpHeaders, @Nullable Object requestBody) throws Exception {
        URI uri = uriComponents.encode().toUri();
        HttpUriRequest request = switch (httpMethod.toString()) {
            case "GET"    -> new HttpGet(uri);
            case "POST"   -> new HttpPost(uri);
            case "PUT"    -> new HttpPut(uri);
            case "DELETE" -> new HttpDelete(uri);
            default -> throw new IllegalArgumentException("Unsupported method: " + httpMethod);
        };

        if (httpHeaders == null) httpHeaders = new HttpHeaders();
        if (!StringUtils.hasText(httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE))) httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE); // CONTENT_TYPE 없을때 디폴트
        RequestUtil.applyRequestHeaders(request, httpHeaders);
        RequestUtil.applyRequestBody(request, requestBody);

        try (CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(request)) {
            HttpHeaders responseHeaders = new HttpHeaders();
            Arrays.stream(closeableHttpResponse.getHeaders())
                    .forEach(header -> responseHeaders.add(header.getName(), header.getValue()));

            String outboundId = LocalDateTime.now().toString();
            String responseBodyStr = EntityUtils.toString(closeableHttpResponse.getEntity(), StandardCharsets.UTF_8);
            HttpClientResponseDto httpClientResponseDto = new HttpClientResponseDto(closeableHttpResponse.getCode(), responseHeaders, responseBodyStr);

            justLogging(requestBody, outboundId, httpMethod, uriComponents, httpHeaders, httpClientResponseDto);
            return httpClientResponseDto;
        }
    }

    /**
     * 메인 설정과 현재 요청 컨텍스트를 기준으로 outbound 상세 로그와 Req/Res 연계 로그를 기록한다.
     */
    private void justLogging(Object requestBody, String outboundId, HttpMethod httpMethod, UriComponents uriComponents, HttpHeaders httpHeaders
            , HttpClientResponseDto httpClientResponseDto) throws Exception {

        // Ounbound 호출 정보 로깅
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
                            , httpClientResponseDto.code(), httpClientResponseDto.headers().toString(), httpClientResponseDto.body());
            String logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_OutboundSupportDetailLog_At_Main.class).get("value"), "");
            log.info(LoggingUtil.makeBaseForm(logTag, "Outbound Support Detail Log", logContent));
        }

        // DetailLog 대상 컨트롤러에서 호출한 Outbound 정보를 함께 남긴다. 스케줄러처럼 request 가 없는 흐름은 제외한다.
        HttpServletRequest currentRequest = SpringUtil.getRequestOrNull();
        if (currentRequest != null) {
            // 컨트롤러 요청 중 발생한 outbound 정보만 Req/Res detail log 에 연결한다.
            // 인터셉터 attribute 가 있으면 우선 사용하고, AOP/예외 흐름 등 보조 케이스는 register 로 한 번 더 확인한다.
            boolean hasReqResDetailLog = MainClassAnnotationRegister.hasAnnotation(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                    || Boolean.TRUE.equals(currentRequest.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED))
                    || requestMappingAnnotationRegister.hasAnnotation(currentRequest, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class);

            if (hasReqResDetailLog) {
                List<String> relatedOutbounds = (List<String>) currentRequest.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS);
                if (relatedOutbounds == null) {
                    relatedOutbounds = new ArrayList<>();
                }
                relatedOutbounds.add(outboundId + " " + httpMethod.name() + " " + uriComponents.toString() + " --> " + httpClientResponseDto.code());
                currentRequest.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS, relatedOutbounds);
            }
        }
    }

    /**
     * @deprecated {@link HttpClientResponseDto#body()} 사용 흐름으로 대체되었으며 기존 HttpEntity 처리 호환용으로만 남겨둔다.
     */
    @Deprecated
    public static String DEPRECATED_convertResponseToString(HttpEntity httpEntity) throws Exception {
        String reponseString =EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        log.debug("responseBody to String = {}", reponseString);

        EntityUtils.consume(httpEntity);
        return reponseString;

        //todo: response 결과를 라인별로 받아서 처리가 필요한 경우 사용 (코드 테스트 필요)
        /*
        try (InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), StandardCharsets.UTF_8)) {
            String responseStr = new BufferedReader(inputStreamReader)
                    .lines()
                    .collect(Collectors.joining("\n"));

            if(httpEntity != null) {EntityUtils.consume(httpEntity);}
            return responseStr;
        }
         */
    }
}
