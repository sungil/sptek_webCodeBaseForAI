package com._sptek.__webFramework.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/**
 * Authorization Bearer JWT가 있는 요청을 Spring Security Authentication으로 변환하는 필터.
 *
 * <p>JWT가 없거나 유효하지 않으면 직접 오류 응답을 만들지 않고 다음 security filter 흐름에 맡긴다.
 * 유효한 JWT만 SecurityContextHolder에 인증 객체로 저장한다.</p>
 */
@Slf4j
public class CustomJwtFilter extends GenericFilterBean {
    // todo: 유효 토큰으로 요청이 온다면 response 로 유효시간을 새로 늘린 토큰을 보내줘야 할까?? 검토 필요, 마찮가지고 sessionId 도 요청때마다 유효시간을 다시 늘려서 내려야 할지 검토 필요

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_PREFIX  = "Bearer ";
    private final GeneralTokenProvider generalTokenProvider;

    public CustomJwtFilter(GeneralTokenProvider generalTokenProvider){
        this.generalTokenProvider = generalTokenProvider;
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
        String requestUri = httpServletRequest.getRequestURI();
        String jwt = getJwtFromRequest(httpServletRequest);

        // 해더에 JWT가 정상적으로 있는경우 authentication을 만들어 SecurityContext에 저장
        if(StringUtils.hasText(jwt) && generalTokenProvider.validateJwt(jwt)){
            Authentication authentication = generalTokenProvider.convertJwtToAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //log.debug("Authentication({}) has been saved in SecurityContextHolder, uri: {}", authentication, requestUri);
        } else {
            //jwt가 없거나 유효하지 않다도 직접 별다른 처리를 하지 않음(다른 필터에 맡김)
            //log.debug("jwt is empty or fail to convert jwt({}) to authentication, uri: {}", jwt, requestUri);
        }
        chain.doFilter(httpServletRequest,response);
    }

    /**
     * Authorization header에서 Bearer prefix를 제거한 JWT 문자열을 추출한다.
     */
    private @Nullable String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTHORIZATION_PREFIX)){
            log.debug("Bearer : " + bearerToken);
            return bearerToken.substring(AUTHORIZATION_PREFIX.length());
        }
        return null;
    }
}
