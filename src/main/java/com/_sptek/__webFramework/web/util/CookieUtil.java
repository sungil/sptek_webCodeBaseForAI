package com._sptek.__webFramework.web.util;

import com._sptek.__webFramework.core.util.SpringUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
/**
 * 쿠키를 생성, 추가, 조회, 삭제하는 보조 유틸리티.
 *
 * <p>신규 코드에서는 필요한 옵션이 명확한 {@link ResponseCookie} builder 직접 사용을 우선하고,
 * 이 클래스는 yml 기본값 적용과 현재 요청/응답 컨텍스트 기반 편의 기능으로 사용한다.</p>
 */
public class CookieUtil {
    private static final boolean DEFAULT_COOKIE_HTTPONLY = true;
    private static final boolean DEFAULT_COOKIE_SECURE = false;
    private static final String DEFAULT_COOKIE_PATH = "/";
    private static final String DEFAULT_COOKIE_SAMESITE = "Lax";

    /**
     * 기본 쿠키 옵션을 적용한 ResponseCookie를 생성한다.
     */
    public static @NotNull ResponseCookie createCookie(@NotNull String name, @NotNull String value, @NotNull Duration maxAge) {
        return createCookie(name, value, maxAge, null, null, null, null, null);
    }

    /**
     * 명시된 옵션과 프레임워크 기본값을 조합해 ResponseCookie를 생성한다.
     */
    public static @NotNull ResponseCookie createCookie(@NotNull String name, @NotNull String value, @NotNull Duration maxAge, Boolean httpOnly, Boolean secure, String domain, String path, String sameSite) {
        CookieProperties.Defaults defaults = defaultCookieProperties();
        if (httpOnly == null) httpOnly = defaults.isHttpOnly();
        if (secure == null) secure = defaults.isSecure();
        if (!StringUtils.hasText(path)) path = defaults.getPath();
        sameSite = decideSameSite(sameSite);

        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .httpOnly(httpOnly)
                .secure(secure)
                .path(path)
                .sameSite(sameSite);
        if (StringUtils.hasText(domain)) cookieBuilder.domain(domain); //domain 은 없는 경우 완전 제외 처러
        return cookieBuilder.build();
    }

    /**
     * 기본 옵션으로 쿠키를 생성해 현재 응답의 Set-Cookie 헤더에 추가한다.
     */
    public static void createCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Duration maxAge) {
        addCookie(createCookie(name, value, maxAge, null, null, null, null, null));
    }

    /**
     * 기본 옵션으로 쿠키를 생성해 지정 응답의 Set-Cookie 헤더에 추가한다.
     */
    public static void createCookieAndAdd(@NotNull HttpServletResponse response, @NotNull String name, @NotNull String value, @NotNull Duration maxAge) {
        addCookie(response, createCookie(name, value, maxAge, null, null, null, null, null));
    }

    /**
     * 지정 옵션으로 쿠키를 생성해 현재 응답의 Set-Cookie 헤더에 추가한다.
     */
    public static void createCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Duration maxAge, Boolean httpOnly, Boolean secure, String domain, String path, String sameSite) {
        addCookie(createCookie(name, value, maxAge, httpOnly, secure, domain, path, sameSite));
    }

    /**
     * 지정 옵션으로 쿠키를 생성해 지정 응답의 Set-Cookie 헤더에 추가한다.
     */
    public static void createCookieAndAdd(@NotNull HttpServletResponse response, @NotNull String name, @NotNull String value, @NotNull Duration maxAge, Boolean httpOnly, Boolean secure, String domain, String path, String sameSite) {
        addCookie(response, createCookie(name, value, maxAge, httpOnly, secure, domain, path, sameSite));
    }

    /**
     * 현재 응답에 ResponseCookie 문자열을 Set-Cookie 헤더로 추가한다.
     */
    public static void addCookie(@NotNull ResponseCookie responseCookie) {
        addCookie(SpringUtil.getResponse(), responseCookie);
        //log.debug("addResponseCookie : {}", responseCookie);
    }

    /**
     * 지정 응답에 ResponseCookie 문자열을 Set-Cookie 헤더로 추가한다.
     */
    public static void addCookie(@NotNull HttpServletResponse response, @NotNull ResponseCookie responseCookie) {
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    /**
     * 현재 요청에서 이름이 일치하는 쿠키 목록을 반환한다.
     */
    public static @NotNull ArrayList<Cookie> getCookies(@NotNull String name) {
        HttpServletRequest request = SpringUtil.getRequestOrNull();
        if (request == null) {
            return new ArrayList<>();
        }
        return getCookies(request, name);
    }

    /**
     * 지정 요청에서 이름이 일치하는 쿠키 목록을 반환한다.
     */
    public static @NotNull ArrayList<Cookie> getCookies(@NotNull HttpServletRequest request, @NotNull String name) {
        Cookie[] cookies = request.getCookies();
        return cookies == null ? new ArrayList<>() :
                Arrays.stream(cookies)
                        .filter(cookie -> name.equals(cookie.getName()))
                        .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 전달된 쿠키 이름들을 현재 응답에서 만료 처리한다.
     */
    public static void deleteCookies(@NotNull String... cookieNames) {
        for(String cookieName : cookieNames){
            deleteCookie(cookieName);
        }
    }

    /**
     * 지정 쿠키를 path "/" 기준으로 즉시 만료 처리한다.
     */
    public static void deleteCookie(@NotNull String name) {
        deleteCookie(name, null, null, null, null, null);
    }

    /**
     * 지정 응답에서 쿠키를 path "/" 기준으로 즉시 만료 처리한다.
     */
    public static void deleteCookie(@NotNull HttpServletResponse response, @NotNull String name) {
        deleteCookie(response, name, null, null, null, null, null);
    }

    /**
     * 지정 옵션으로 생성된 쿠키와 같은 domain/path 조건을 사용해 현재 응답에서 즉시 만료 처리한다.
     */
    public static void deleteCookie(@NotNull String name, Boolean httpOnly, Boolean secure, String domain, String path, String sameSite) {
        addCookie(createCookie(name, "", Duration.ZERO, httpOnly, secure, domain, path, sameSite));
    }

    /**
     * 지정 옵션으로 생성된 쿠키와 같은 domain/path 조건을 사용해 지정 응답에서 즉시 만료 처리한다.
     */
    public static void deleteCookie(@NotNull HttpServletResponse response, @NotNull String name, Boolean httpOnly, Boolean secure, String domain, String path, String sameSite) {
        addCookie(response, createCookie(name, "", Duration.ZERO, httpOnly, secure, domain, path, sameSite));
    }

    /**
     * SameSite 옵션을 표준 대소문자 값으로 정규화한다.
     */
    private static String decideSameSite(String sameSite) {
        if (!StringUtils.hasText(sameSite)) sameSite = defaultCookieProperties().getSameSite();
        String v = sameSite.trim().toLowerCase(Locale.ROOT);
        return switch (v) {
            case "lax" -> "Lax";
            case "strict" -> "Strict";
            case "none" -> "None";
            default -> throw new IllegalArgumentException("Invalid SameSite: " + sameSite);
        };
    }

    private static CookieProperties.Defaults defaultCookieProperties() {
        try {
            return SpringUtil.getSpringBean(CookieProperties.class).getDefaults();
        } catch (RuntimeException e) {
            CookieProperties.Defaults fallback = new CookieProperties.Defaults();
            fallback.setHttpOnly(DEFAULT_COOKIE_HTTPONLY);
            fallback.setSecure(DEFAULT_COOKIE_SECURE);
            fallback.setPath(DEFAULT_COOKIE_PATH);
            fallback.setSameSite(DEFAULT_COOKIE_SAMESITE);
            return fallback;
        }
    }
}
