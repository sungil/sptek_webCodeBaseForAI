package com._sptek.__webFramework.observability.logging;

import com._sptek.__webFramework.web.filter.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import com._sptek.__webFramework.core.constant.CommonConstants;
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
     *
     * <p>async dispatch에서는 첫 번째 응답 객체를 보관해 최종 dispatch에서 body 복사가 누락되지 않게 한다.</p>
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //request, response을 ContentCachingRequestWrapper, ContentCachingResponseWrapper 로 변환 하여 하위 플로우 로 넘긴다.(req, res 의 body를 여러번 읽기 위한 용도로 활용됨)

        // 필터 제외 케이스. 컨트롤러 어노테이션 여부는 아직 HandlerMethod 가 확정되기 전이라 여기서 판단하지 않는다.
        boolean isMinorRequest = MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)
                && (SecurityUtil.isNotEssentialRequest(request) || SecurityUtil.isStaticResourceRequest(request));
        if (isMinorRequest) {
            filterChain.doFilter(request, response);
            return;
        }

        // 필터 적용 전 (ContentCachingWrapper 상태가 아니라면 래핑)
        var contentCachingRequestWrapper = request instanceof ContentCachingRequestWrapper ? (ContentCachingRequestWrapper)request : new ContentCachingRequestWrapper(request);
        var amIContentCachingResponseWrapperOwner = !(response instanceof ContentCachingResponseWrapper);
        var contentCachingResponseWrapper = amIContentCachingResponseWrapperOwner ? new ContentCachingResponseWrapper(response) : (ContentCachingResponseWrapper)response;

        // todo: 중요! Async 디스패치의 두번째 호출일때 새로 들어온 response 의 경우 copyBodyToResponse 가 동작하지 않는 현상이 있음, 해결 방안으로 첫번째 호출때의 response를 저장해서 사용함
        if (amIContentCachingResponseWrapperOwner && request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE) == null) {
            request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE, response);
        }

        // todo: 중요! Async 디스패치의 두번째 호출일때 새로 들어온 response 의 경우 copyBodyToResponse 가 동작하지 않는 현상이 있음, 해결 방안으로 첫번째 호출때의 response를 저장해서 사용함
        if (amIContentCachingResponseWrapperOwner && isAsyncDispatch(request)) {
            contentCachingResponseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE));
        }

        try {
            filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
            // isAsyncStarted(request) = Async 를 요청해놓고 돌아온 케이스 인지 (다시말해 !isAsyncStarted(request) = Async 가 아닌 그냥 일반 요청)
            // isAsyncDispatch(request) = Async 요청을 마무리하고 돌아온 케이스(두번째 호출) 인지
            // todo: 중요! Async 디스패치의 첫번째 호출일때는 처리 의미 없음
            if (!isAsyncStarted(request) || isAsyncDispatch(request)) {
                // 로그 대상 여부는 ReqResDetailLogDecisionInterceptor 가 실제 HandlerMethod 기준으로 설정한다.
                // 필터는 URL 재매칭을 하지 않고 request/response body 캡처와 최종 출력만 담당한다.
                if (Boolean.TRUE.equals(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED))) {
                    LoggingUtil.reqResDetailLogging(log, contentCachingRequestWrapper, contentCachingResponseWrapper, "Req Res Detail Log From " + this.getClass().getSimpleName());
                }
            } else {
                log.debug("First Async Dispatcher called.");
            }
        } finally {
            // todo: 중요! contentCachingResponseWrapper 을 자신이 직접 생성 했다면 필터 체인 이후 response body 복사 (필수)
            if ((!isAsyncStarted(request) || isAsyncDispatch(request)) && amIContentCachingResponseWrapperOwner) {
                contentCachingResponseWrapper.copyBodyToResponse();
            }
        }
    }

    /**
     * async 재디스패치에서도 동일한 상세 로그 처리 흐름을 타게 한다.
     */
    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
}
