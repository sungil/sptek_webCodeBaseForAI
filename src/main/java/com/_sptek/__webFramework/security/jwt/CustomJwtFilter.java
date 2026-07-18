package com._sptek.__webFramework.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * Authorization Bearer JWT가 있는 요청을 Spring Security Authentication으로 변환하는 필터.
 *
 * <p>JWT가 없으면 익명 요청으로 다음 filter에 맡기고, Authorization header가 있지만 형식이 틀리거나
 * 유효하지 않은 JWT는 AuthenticationEntryPoint에 위임해 API 공통 401 응답으로 종료한다.</p>
 */
@Slf4j
public class CustomJwtFilter extends GenericFilterBean {
    // todo: 유효 토큰으로 요청이 온다면 response 로 유효시간을 새로 늘린 토큰을 보내줘야 할까?? 검토 필요, 마찮가지고 sessionId 도 요청때마다 유효시간을 다시 늘려서 내려야 할지 검토 필요

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_PREFIX  = "Bearer ";
    private final GeneralTokenProvider generalTokenProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public CustomJwtFilter(GeneralTokenProvider generalTokenProvider, AuthenticationEntryPoint authenticationEntryPoint){
        this.generalTokenProvider = generalTokenProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * JWT를 전달하는 표준 request header 이름을 반환한다.
     */
    public static String getAuthorizationHeader() {
        return AUTHORIZATION_HEADER;
    }

    /**
     * Bearer token prefix를 반환한다.
     */
    public static String getAuthorizationPrefix() {
        return AUTHORIZATION_PREFIX;
    }

    /**
     * request header의 JWT가 유효하면 Authentication으로 변환해 SecurityContext에 저장한다.
     */
    // 해더에 jwt가 있는 경우 jwt를 authentication으로 변환하여 SecurityContextHolder에 저장하는 역할을 해야함
    // 비인증 상태로 JWT가 없거나 유효하지 않은경우 SecurityContext에 저장되는게 없음
    // 적접 에러 처리를 하지 않고 다른 필터에 의해 인증 필요 페이지로 갔는데 SecurityContext에 authentication이 없는 경우 그때 EX가 발생함
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String requestUri = httpServletRequest.getRequestURI();
        String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);

        if (!StringUtils.hasText(authorizationHeader)) {
            chain.doFilter(httpServletRequest, response);
            return;
        }

        if (!authorizationHeader.startsWith(AUTHORIZATION_PREFIX)) {
            failAuthentication(httpServletRequest, httpServletResponse, "Invalid Authorization header. uri: " + requestUri);
            return;
        }

        String jwt = authorizationHeader.substring(AUTHORIZATION_PREFIX.length()).trim();
        if (!StringUtils.hasText(jwt)) {
            failAuthentication(httpServletRequest, httpServletResponse, "JWT token is empty. uri: " + requestUri);
            return;
        }

        try {
            if (!generalTokenProvider.validateJwt(jwt)) {
                failAuthentication(httpServletRequest, httpServletResponse, "Invalid JWT token. uri: " + requestUri);
                return;
            }

            Authentication authentication = generalTokenProvider.convertJwtToAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //log.debug("Authentication({}) has been saved in SecurityContextHolder, uri: {}", authentication, requestUri);
            chain.doFilter(httpServletRequest,response);
        } catch (RuntimeException ex) {
            failAuthentication(httpServletRequest, httpServletResponse, "Failed to authenticate JWT token. uri: " + requestUri);
        }
    }

    private void failAuthentication(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        authenticationEntryPoint.commence(request, response, new BadCredentialsException(message));
    }
}
