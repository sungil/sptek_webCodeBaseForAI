package com._sptek.__webFramework.observability.mdc;

import com._sptek.__webFramework.security.SecurityConstants;
import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.security.util.AuthenticationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * 요청 단위 로그 추적을 위해 MDC 값을 설정하고 정리하는 필터.
 *
 * <p>로그 패턴에서 memberId, sessionId, correlationId를 사용할 수 있게 하며,
 * 요청 종료 후에는 thread 재사용에 따른 오염을 막기 위해 MDC를 반드시 비운다.</p>
 */
@Slf4j
//@Profile(value = { "local", "dev", "stg" })
//@HasAnnotationOnMain_InBean(EnableMdcTagging_InMain.class)
//@WebFilter(urlPatterns = "/*")
public class MakeMdcFilter extends OncePerRequestFilter {
    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(LoggingConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * 현재 요청의 사용자/세션/correlationId를 MDC에 저장하고 다음 필터로 전달한다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 요청 단위 로그 추적을 위해 MDC에 사용자/세션/correlationId를 넣는다.
        // MDC는 thread-local 기반이므로 요청 종료 시 반드시 clear해야 한다.
        try {
            HttpSession session = request.getSession(false);
            MDC.put("memberId", AuthenticationUtil.isRealLogin() ? mask(AuthenticationUtil.getMyName(), 4) : SecurityConstants.ANONYMOUS_USER);
            MDC.put("sessionId", session != null ? mask(session.getId(), 8) : "");

            // 분산 시스템에서 API 호출 흐름을 trace 하기 위한 값으로 추후 사용을 위해 적용함
            String correlationId = request.getHeader("Correlation-Id");
            if (correlationId == null) {
                correlationId = Objects.toString(request.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_CORRELATION_ID), UUID.randomUUID().toString());
                request.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_CORRELATION_ID, correlationId);
                response.setHeader("Correlation-Id", correlationId);
            }
            MDC.put("correlationId", correlationId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear(); // 요청이 끝난 뒤 반드시 MDC 정리
        }
    }

    /**
     * 로그 추적에 필요한 앞부분만 남기고 나머지는 노출하지 않는다.
     */
    private String mask(String value, int visibleLength) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.substring(0, Math.min(value.length(), visibleLength)) + "**";
    }
}

