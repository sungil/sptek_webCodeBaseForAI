package com._sptek.__webFramework.web.locale;

import com._sptek.__webFramework.web.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.datetime.standard.DateTimeContext;
import org.springframework.format.datetime.standard.DateTimeContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

/**
 * locale 변경 처리와 timezone cookie 관리를 함께 수행하는 LocaleChangeInterceptor 확장 구현.
 *
 * <p>locale query parameter가 있으면 Spring 기본 locale 변경 흐름을 사용하고,
 * parameter가 없지만 locale cookie가 있으면 cookie 만료 연장을 위해 다시 설정한다.
 * timezone은 LocaleContextHolder와 DateTimeContextHolder에 함께 반영한다.</p>
 */
public class CustomLocaleChangeInterceptor extends LocaleChangeInterceptor {
    private final LocaleProperties localeProperties;

    public CustomLocaleChangeInterceptor(LocaleProperties localeProperties) {
        this.localeProperties = localeProperties;
    }

    /**
     * locale/timezone request parameter 또는 cookie를 기준으로 현재 요청의 locale context를 갱신한다.
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws ServletException {
        if (request.getParameter(getParamName()) != null) {
            super.preHandle(request, response, handler); // locale Param 이 있을 경우 내부에서 setLocale 및 cookie 까지 생성

        } else {
            // cookie 만 있다면 maxAge 연장을 위해 재 생성
            List<Cookie> localeCookies = CookieUtil.getCookies(localeProperties.getCookie().getLocaleName());
            if (!localeCookies.isEmpty()) {
                LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
                if (localeResolver != null) {
                    // 내부에서 setLocale 및 cookie 까지 재 생성
                    localeResolver.setLocale(request, response, super.parseLocaleValue(localeCookies.get(0).getValue()));
                }
            }
        }

        // locale 처리시 LocaleContextHolder의 timezone 처리도 함께 처리하려고 custom 클레스로 만듬.
        // DateTimeContextHolder 가 따로 있지만.. 대부분의 영역에서 DateTimeContextHolder 가 없으면 LocaleContextHolder의 timezone 을 사용함
        String timeZoneValue = request.getParameter(localeProperties.getCookie().getTimezoneName());
        List<Cookie> timeZoneCookies = CookieUtil.getCookies(localeProperties.getCookie().getTimezoneName());
        if (timeZoneValue == null && !timeZoneCookies.isEmpty()) {
            timeZoneValue = timeZoneCookies.get(0).getValue();
        }

        if (timeZoneValue != null) {
            applyTimeZoneIfValid(timeZoneValue);
        }

        return true;
    }

    private void applyTimeZoneIfValid(String timeZoneValue) {
        try {
            ZoneId zoneId = ZoneId.of(timeZoneValue);
            TimeZone timeZone = TimeZone.getTimeZone(zoneId);
            LocaleContextHolder.setTimeZone(timeZone);
            CookieUtil.createCookieAndAdd(
                    localeProperties.getCookie().getTimezoneName(),
                    timeZoneValue,
                    Duration.ofDays(localeProperties.getCookie().getMaxAgeDays()),
                    localeProperties.getCookie().isHttpOnly(),
                    localeProperties.getCookie().isSecure(),
                    null,
                    null,
                    localeProperties.getCookie().getSameSite()
            );

            // DateTimeContextHolder 까지 추가로 설정
            var ctx = Optional.ofNullable(DateTimeContextHolder.getDateTimeContext()).orElseGet(DateTimeContext::new);
            ctx.setTimeZone(zoneId);
            DateTimeContextHolder.setDateTimeContext(ctx);
        } catch (DateTimeException ignored) {
            // 외부 입력으로 들어온 잘못된 timezone은 사용자 편의 설정 실패로 보고 현재 요청 흐름은 유지한다.
        }
    }
}
