package com._sptek.__webFramework.observability.processTime;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.web.filter.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.security.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;


/**
 * 요청 처리 시작 시각을 request attribute에 기록하는 필터.
 *
 * <p>필터, 인터셉터, 예외 처리기 등 후속 단계에서 동일한 요청 기준 시각을 사용해야 할 때
 * {@link LoggingConstants#REQ_ATTRIBUTE_FOR_LOGGING_TIMESTAMP} attribute를 참조한다.</p>
 */
@Slf4j
//@Profile(value = { "local", "dev", "stg", "prd" })
//@WebFilter(urlPatterns = "/api/*") //ant 표현식 사용 불가 ex: /**
public class MakeRequestTimestampFilter extends OncePerRequestFilter {

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(LoggingConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * minor request가 아니면 현재 시각을 request attribute에 저장한 뒤 필터 체인을 계속 진행한다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //log.debug("MakeRequestTimestampFilter start");

        if (MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)) {
            if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        request.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_TIMESTAMP, LocalDateTime.now());
        filterChain.doFilter(request, response);
    }
}

