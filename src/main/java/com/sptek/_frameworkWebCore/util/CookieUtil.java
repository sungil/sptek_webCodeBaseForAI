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
/*
CookieUtil 을 사용하지 않고 ResponseCookie 객체를 직접 builder 방식으로 사용하는게 가장 좋음.(익숙하지 않은사람을 위해 남김)
*/

@Slf4j
public class CookieUtil {
    private static final boolean DEFAULT_COOKIE_HTTPONLY = true;
    private static final boolean DEFAULT_COOKIE_SECURE = false;
    private static final String DEFAULT_COOKIE_PATH = "/";
    private static final String DEFAULT_COOKIE_SAMESITE = "Lax";

    public static @NotNull ResponseCookie createCookie(@NotNull String name, @NotNull String value, @NotNull Duration maxAge) {
        return createCookie(name, value, maxAge, null, null, null, null, null);
    }

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

    public static void createCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Duration maxAge) {
        addCookie(createCookie(name, value, maxAge, null, null, null, null, null));
    }

    public static void createCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Duration maxAge, Boolean httpOnly, Boolean secure, String domain, String path, String sameSite) {
        addCookie(createCookie(name, value, maxAge, httpOnly, secure, domain, path, sameSite));
    }

    public static void addCookie(@NotNull ResponseCookie responseCookie) {
        SpringUtil.getResponse().addHeader("Set-Cookie", responseCookie.toString());
        //log.debug("addResponseCookie : {}", responseCookie);
    }

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

    public static void deleteCookies(@NotNull String... cookieNames) {
        for(String cookieName : cookieNames){
            deleteCookie(cookieName);
        }
    }

    public static void deleteCookie(@NotNull String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        SpringUtil.getResponse().addCookie(cookie);
    }

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
