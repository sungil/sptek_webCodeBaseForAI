package com._sptek.__webFramework.observability.logging;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com._sptek.__webFramework.core.constant.CommonConstants;
import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.web.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/*
UV 관련 처리를 위한 인터셉터
 */
/**
 * View 요청 기준의 방문자 신규/재방문 로그를 남기는 interceptor.
 *
 * <p>방문 이력 cookie가 없으면 신규 방문, 있으면 기존 방문으로 판단한다.
 * cookie는 당일 자정까지 유효하게 갱신해 일 단위 UV 로그 기준으로 사용한다.</p>
 */
@HasAnnotationOnMain_At_Bean(Enable_VisitHistoryLog_At_Main.class)
@Slf4j
@Component

public class VisitHistoryLoggingInterceptor implements HandlerInterceptor{

    /**
     * 방문 이력 cookie를 확인해 방문 로그를 남기고, 당일 만료 cookie를 갱신한다.
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        String visitHistoryLog = Optional.ofNullable(CookieUtil.getCookies(CommonConstants.VISIT_HISTORY_COOKIE_NAME))
                .filter(cookies -> !cookies.isEmpty())
                .map(cookies -> CommonConstants.VISIT_HISTORY_EXIST_VISITOR_LOG)
                .orElse(CommonConstants.VISIT_HISTORY_NEW_VISITOR_LOG);

        //로그를 남기는게 주 역함임으로 아래 주석 처리 하지 않도록! (console 에는 로그 처리 되지 않음)
        String logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_VisitHistoryLog_At_Main.class).get("value"), "");
        log.info(LoggingUtil.makeSimpleForm(logTag, visitHistoryLog));

        //오늘 까지 유효한 쿠키로 생성 (자정 까지 남은 sec), 이미 쿠키가 있는 경우에도 새로 생성 (쿠키 유효기간을 변경한 경우 바로 적용되게 하기 위해)
        LocalDateTime now = LocalDateTime.now();
        Duration maxAge = Duration.between(now, now.toLocalDate().plusDays(1).atStartOfDay());
        CookieUtil.createCookieAndAdd(CommonConstants.VISIT_HISTORY_COOKIE_NAME, CommonConstants.VISIT_HISTORY_COOKIE_VALE, maxAge);
        return true;
    }

}

