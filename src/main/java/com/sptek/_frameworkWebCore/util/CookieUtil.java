package com.sptek._frameworkWebCore.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
/**
 * 현재 요청/응답 컨텍스트에서 쿠키를 생성, 추가, 조회, 삭제하는 보조 유틸리티.
 *
 * <p>신규 코드에서는 필요한 옵션이 명확한 {@link ResponseCookie} builder 직접 사용을 우선하고,
 * 이 클래스는 반복되는 기본값 적용과 현재 응답에 추가하는 편의 기능으로 사용한다.</p>
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
        if (httpOnly == null) httpOnly = DEFAULT_COOKIE_HTTPONLY;
        if (secure == null) secure = DEFAULT_COOKIE_SECURE;
        if (!StringUtils.hasText(path)) path = DEFAULT_COOKIE_PATH;
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
     * 지정 옵션으로 쿠키를 생성해 현재 응답의 Set-Cookie 헤더에 추가한다.
     */
    public static void createCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Duration maxAge, Boolean httpOnly, Boolean secure, String domain, String path, String sameSite) {
        addCookie(createCookie(name, value, maxAge, httpOnly, secure, domain, path, sameSite));
    }

    /**
     * 현재 응답에 ResponseCookie 문자열을 Set-Cookie 헤더로 추가한다.
     */
    public static void addCookie(@NotNull ResponseCookie responseCookie) {
        SpringUtil.getResponse().addHeader("Set-Cookie", responseCookie.toString());
        //log.debug("addResponseCookie : {}", responseCookie);
    }

    /**
     * 현재 요청에서 이름이 일치하는 쿠키 목록을 반환한다.
     */
    public static @NotNull ArrayList<Cookie> getCookies(@NotNull String name) {
        HttpServletRequest request = SpringUtil.getRequestOrNull();
        if (request == null) {
            return new ArrayList<>();
        }
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
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        SpringUtil.getResponse().addCookie(cookie);
    }

    /**
     * SameSite 옵션을 표준 대소문자 값으로 정규화한다.
     */
    private static String decideSameSite(String sameSite) {
        if (!StringUtils.hasText(sameSite)) return DEFAULT_COOKIE_SAMESITE; // 기본값
        String v = sameSite.trim().toLowerCase(Locale.ROOT);
        return switch (v) {
            case "lax" -> "Lax";
            case "strict" -> "Strict";
            case "none" -> "None";
            default -> throw new IllegalArgumentException("Invalid SameSite: " + sameSite);
        };
    }
}
