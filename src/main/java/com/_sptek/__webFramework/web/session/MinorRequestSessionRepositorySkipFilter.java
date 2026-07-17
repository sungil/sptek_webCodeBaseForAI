package com._sptek.__webFramework.web.session;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
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

/**
 * 중요도가 낮은 요청에서 Spring Session 저장소 접근을 줄이는 필터.
 *
 * <p>static resource나 favicon 같은 minor request에 대해 Spring Session의 FILTERED attribute를 미리 설정해
 * Redis 같은 외부 session repository 접근을 건너뛰게 하는 용도다.</p>
 */
@Slf4j
//@Profile(value = { "local", "dev", "stg", "prd" })
//@HasAnnotationOnMain_InBean(EnableNoFilterAndSessionForMinorRequest_InMain.class)
//@WebFilter(urlPatterns = "/*")
public class MinorRequestSessionRepositorySkipFilter extends OncePerRequestFilter {
    private static final String SPRING_SESSION_REPOSITORY_FILTER_CLASS_NAME = "org.springframework.session.web.http.SessionRepositoryFilter";
    private static final String ALREADY_FILTERED_ATTRIBUTE_SUFFIX = ".FILTERED";
    static final String SPRING_SESSION_REPOSITORY_FILTERED_ATTRIBUTE =
            SPRING_SESSION_REPOSITORY_FILTER_CLASS_NAME + ALREADY_FILTERED_ATTRIBUTE_SUFFIX;
    /*
     * spring-session-data-redis 사용 시 SessionRepositoryFilter가 자동 등록되어
     * 기본 HttpSession 대신 Redis 등 외부 저장소 기반 세션 처리를 담당한다.
     *
     * Spring Session은 동일 요청에서 필터가 중복 실행되지 않도록
     * "org.springframework.session.web.http.SessionRepositoryFilter.FILTERED" request attribute를 사용한다.
     * 이 필터는 해당 메커니즘을 이용해 static 리소스 같은 minor request에 attribute를 미리 설정하여
     * 불필요한 Redis 세션 접근을 차단한다.
     *
     * NOTE: Redis 기반 Spring Session 연동 후 실제 동작 검증 필요.
     */

    /**
     * minor request이면 Spring Session 필터가 이미 처리된 것처럼 request attribute를 설정한다.
     */
    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(LoggingConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * static 또는 중요도가 낮은 요청에는 session repository filter 중복 처리를 차단하고 다음 필터로 넘긴다.
     */
    @Override
     public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 중용하지 않은 req 에 대해 session 비 생성 처리
        if (SecurityUtil.isNotEssentialRequest(request) || SecurityUtil.isStaticResourceRequest(request)) {
            request.setAttribute(SPRING_SESSION_REPOSITORY_FILTERED_ATTRIBUTE, Boolean.TRUE); //세션 처리를 끝낸것 처럼 강제 세팅함
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }
}

