package com._sptek.__webFramework.web.cors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 프레임워크 CORS 정책 설정을 API 요청에 적용하는 필터.
 *
 * <p>Spring Security의 기본 CORS 처리 대신 프로젝트 설정값을 직접 읽어 응답 헤더를 구성한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class CorsPolicyFilter extends OncePerRequestFilter {
    private static final String ORIGIN = "Origin";
    private static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    private static final String VARY = "Vary";

    private final CorsProperties corsProperties;

    /**
     * 허용 목록에 포함된 Origin에만 CORS 응답 헤더를 설정한다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String origin = request.getHeader(ORIGIN);
        if (origin == null || origin.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!corsProperties.isOriginAllowed(origin)) {
            log.debug("CORS policy validation denied. origin={}", origin);
            if (isPreflightRequest(request)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        setCorsHeaders(response, origin);
        log.debug("CORS policy validation passed. origin={}", origin);

        if (isPreflightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod())
                && request.getHeader(ACCESS_CONTROL_REQUEST_METHOD) != null;
    }

    private void setCorsHeaders(HttpServletResponse response, String origin) {
        String allowOrigin = corsProperties.allowsAllOrigins() ? "*" : origin;
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin);
        response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, String.join(", ", corsProperties.getAllowedMethods()));
        response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, String.join(", ", corsProperties.getAllowedHeaders()));
        response.setHeader(ACCESS_CONTROL_MAX_AGE, String.valueOf(corsProperties.getMaxAge()));

        if (corsProperties.isAllowCredentials()) {
            response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
        if (!"*".equals(allowOrigin)) {
            response.addHeader(VARY, ORIGIN);
        }
    }
}
