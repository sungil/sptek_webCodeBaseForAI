package com._sptek.__webFramework.observability.logging;

import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.core.constant.CommonConstants;
import com._sptek.__webFramework.security.util.SecurityUtil;
import com._sptek.__webFramework.web.filter.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

/**
 * 요청/응답 body를 캐싱 wrapper로 감싸 상세 로그 출력에 필요한 내용을 보존하는 필터.
 *
 * <p>필터 단계에서는 HandlerMethod를 알 수 없으므로 로그 대상 여부를 직접 판단하지 않는다.
 * {@code ReqResDetailLogDecisionInterceptor}가 request attribute로 남긴 결정값을 기준으로 최종 로그를 출력한다.</p>
 */
@Slf4j
//@Profile(value = { "local", "dev", "stg", "prd" })
//@WebFilter(urlPatterns = "/*")
public class ReqResDetailLogFilter extends OncePerRequestFilter {
    // todo: 어노테이션 속성값을 통해 파일 저장하는 기능 추가 (속성값을 로그 맨 앞 프리픽스로 만들어야 함)

    @PostConstruct
    public void init() {
        //log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * 요청/응답을 ContentCaching wrapper로 보장하고, 처리 완료 시점에 선택된 요청만 상세 로그로 남긴다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        boolean isMinorRequest = MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)
                && (SecurityUtil.isNotEssentialRequest(request) || SecurityUtil.isStaticResourceRequest(request));
        if (isMinorRequest) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper contentCachingRequestWrapper = request instanceof ContentCachingRequestWrapper wrapper
                ? wrapper
                : new ContentCachingRequestWrapper(request);
        boolean isResponseWrapperOwner = !(response instanceof ContentCachingResponseWrapper);
        ContentCachingResponseWrapper contentCachingResponseWrapper = isResponseWrapperOwner
                ? new ContentCachingResponseWrapper(response)
                : (ContentCachingResponseWrapper) response;

        try {
            filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
            if (Boolean.TRUE.equals(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED))) {
                LoggingUtil.reqResDetailLogging(log, contentCachingRequestWrapper, contentCachingResponseWrapper, "Req Res Detail Log From " + this.getClass().getSimpleName());
            }
        } finally {
            if (isResponseWrapperOwner) {
                contentCachingResponseWrapper.copyBodyToResponse();
            }
        }
    }
}
