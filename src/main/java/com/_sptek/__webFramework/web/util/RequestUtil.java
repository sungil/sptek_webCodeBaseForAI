package com._sptek.__webFramework.web.util;

import com._sptek.__webFramework.core.constant.CommonConstants;
import com._sptek.__webFramework.observability.processTime.ExcuteTimeDto;
import com._sptek.__webFramework.core.util.SpringUtil;
import com._sptek.__webFramework.core.util.TypeConvertUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
/**
 * 현재 HTTP request의 URL, 파라미터, 헤더, 본문, 시간 추적 정보와 outbound 요청 조립을 지원하는 유틸리티.
 */
public class RequestUtil {

    /**
     * request scheme, host, port를 조합해 현재 요청 도메인을 반환한다.
     */
    public static String getRequestDomain(@NotNull HttpServletRequest request) {
        String domain = request.getScheme() + "://" + request.getServerName();

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            domain += ":" + request.getServerPort();
        }

        return domain;
    }

    /**
     * query string을 포함한 전체 요청 URL을 반환한다.
     */
    public static @NotNull String getRequestUrlQuery(@NotNull HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }

    /**
     * request의 HTTP method 이름을 반환한다.
     */
    public static String getRequestMethodType(@NotNull HttpServletRequest request) {
        return request.getMethod();
    }

    /**
     * request parameter map을 그대로 반환한다.
     */
    public static Map<String, String[]> getRequestParameterMap(@NotNull HttpServletRequest request) {
        return request.getParameterMap();
    }

    /**
     * 프록시 헤더를 순서대로 확인해 클라이언트 IP로 사용할 값을 추출한다.
     */
    public static String getReqUserIp(@NotNull HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (!StringUtils.hasText(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.length() > 23) { // IPv6
            ip = ip.substring(0, 23);
        }

        log.debug("final requester ip : {}", ip);
        return ip;
    }

    /**
     * 현재 request session이 있으면 지정 attribute 값을 조회하고, 없으면 null을 반환한다.
     */
    public static @Nullable Object getSessionAttribute(@NotNull HttpServletRequest request, String attributeName) {
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(attributeName) : null;
    }

    /**
     * request session의 모든 attribute를 Map으로 복사하고, session이 없으면 빈 Map을 반환한다.
     */
    public static @NotNull Map<String, Object> getSessionAttributesAll(@NotNull HttpServletRequest request, boolean create) {
        HttpSession session = request.getSession(create);
        if (session == null) return Collections.emptyMap();
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            attributes.put(attributeName, session.getAttribute(attributeName));
        }

        return attributes;
    }

    /**
     * request header를 delimiter 없이 Map으로 복사한다.
     */
    public static HashMap<String, String> getRequestHeaderMap(HttpServletRequest request){
        return getRequestHeaderMap(request, "");
    }

    /**
     * request header들을 Map으로 복사하고 각 헤더 값 뒤에 지정 delimiter를 붙인다.
     */
    public static HashMap<String, String> getRequestHeaderMap(HttpServletRequest request, String delimiter) {
        StringBuilder headerString = new StringBuilder();
        HashMap<String, String> headers = new HashMap<>();

        // 요청 헤더 이름을 가져오기
        Set<String> headerNames = TypeConvertUtil.enumerationToSet(request.getHeaderNames());
        // 모든 헤더를 순회하며 로그로 남기기
        for (String headerName : headerNames) {
            Enumeration<String> headerValues = request.getHeaders(headerName);

            // 헤더 값을 리스트 형태로 변환하여 출력
            StringBuilder values = new StringBuilder();
            while (headerValues.hasMoreElements()) {
                values.append(headerValues.nextElement()).append(", ");
            }

            // 마지막 쉼표와 공백 제거
            if (values.length() > 0) {
                values.setLength(values.length() - 2);  // 마지막 쉼표와 공백 제거
            }

            // 최종 문자열에 추가
            //headerString.append(headerName).append(" = ").append(values.toString()).append("\n");
            headers.put(headerName, values.append(delimiter).toString());
        }
        return headers;
    }

    /**
     * Spring HttpHeaders 값을 Apache HttpClient 요청 객체에 추가한다.
     */
    public static void applyRequestHeaders(HttpUriRequest httpUriRequest, @Nullable HttpHeaders httpHeaders) {
        if (httpHeaders == null || httpHeaders.isEmpty()) return;
        httpHeaders.forEach((name, values) ->
                values.forEach(value -> httpUriRequest.addHeader(name, value))
        );
    }

    /**
     * 문자열 또는 객체 request body를 JSON 문자열로 변환해 Apache HttpClient 요청 entity에 설정한다.
     */
    public static void applyRequestBody(HttpUriRequest httpUriRequest, @Nullable Object requestBody) throws Exception {
        if (requestBody == null) return;
        String requestBodyString = requestBody instanceof String ? requestBody.toString() : TypeConvertUtil.objectToJsonWithoutRootName(requestBody, false);
        if (StringUtils.hasText(requestBodyString)) httpUriRequest.setEntity(new StringEntity(requestBodyString, StandardCharsets.UTF_8));
    }

    /**
     * 요청 시작 attribute와 현재 시각을 비교해 요청 처리 시간 DTO를 만든다.
     */
    public static ExcuteTimeDto traceRequestDuration() {
        String startTime = Optional.ofNullable(SpringUtil.getRequestOrNull())
                .map(request -> request
                        .getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_TIMESTAMP)).map(Object::toString).orElse("");

        if (!StringUtils.hasText(startTime)) {
            return new ExcuteTimeDto("N/A", LocalDateTime.now().toString(), "N/A");
        }

        String currentTime = LocalDateTime.now().toString();
        String durationMsec = Objects.toString(Duration.between(LocalDateTime.parse(startTime), LocalDateTime.parse(currentTime)).toMillis(), "");
        return new ExcuteTimeDto(startTime, currentTime, durationMsec);
    }

    /**
     * ContentCachingRequestWrapper에 저장된 요청 본문을 로그용 문자열로 반환한다.
     */
    public static String getRequestBody(ContentCachingRequestWrapper contentCachingRequestWrapper) {
        byte[] content = contentCachingRequestWrapper.getContentAsByteArray();
        if (content.length == 0) return "";
        try {
            return "\n" + new String(content, contentCachingRequestWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "N/A (Unsupported Encoding)";
        }
    }

    /**
     * 일반 API와 시스템 지원 API 요청인지 URI와 error dispatch URI를 기준으로 판별한다.
     */
    public static boolean isApiRequest(@NotNull HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String errorRequestUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .map(Object::toString)
                .orElse("");
        return  (requestUri.startsWith("/api/") || requestUri.startsWith("/systemSupportApi/") || errorRequestUri.startsWith("/api/") || errorRequestUri.startsWith("/systemSupportApi/"));
    }
}

