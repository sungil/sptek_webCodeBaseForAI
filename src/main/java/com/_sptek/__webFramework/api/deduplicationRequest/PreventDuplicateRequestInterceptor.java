package com._sptek.__webFramework.api.deduplicationRequest;

import com._sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import com._sptek.__webFramework.core.code.CommonErrorCodeEnum;
import com._sptek.__webFramework.core.constant.CommonConstants;
import com._sptek.__webFramework.core.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 중복 요청 방지 애노테이션이 붙은 API HandlerMethod에 요청 단위 de-duplication을 적용하는 interceptor.
 *
 * <p>URL 재매칭이 아니라 {@link RequestMappingAnnotationRegister}가 수집한 HandlerMethod 애노테이션을 기준으로 판단한다.
 * 같은 세션 안에서도 HandlerMethod, HTTP method, URI, query string이 같은 요청만 중복으로 차단한다.
 * 차단 시간은 애노테이션 속성의 {@code maxMs}, {@code minMs}를 우선 사용한다.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PreventDuplicateRequestInterceptor implements HandlerInterceptor {
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;

    /**
     * 대상 HandlerMethod이면 요청별 key의 만료 시각을 확인해 동일 API 중복 요청만 차단한다.
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) return true;

        Optional<DuplicateRequestPolicy> duplicateRequestPolicyOptional = getDuplicateRequestPolicy(handlerMethod);
        if (duplicateRequestPolicyOptional.isEmpty()) return true;

        DuplicateRequestPolicy duplicateRequestPolicy = duplicateRequestPolicyOptional.get();
        DuplicateRequestKey duplicateRequestKey = DuplicateRequestKey.from(request, handlerMethod);
        Map<DuplicateRequestKey, Long> duplicateRequestExpireTimes = getDuplicateRequestExpireTimes(request);
        long now = System.currentTimeMillis();
        AtomicBoolean accepted = new AtomicBoolean(false);

        duplicateRequestExpireTimes.compute(duplicateRequestKey, (key, expiredTimeMs) -> {
            if (expiredTimeMs == null || expiredTimeMs < now) {
                accepted.set(true);
                return now + duplicateRequestPolicy.maxMs();
            }
            return expiredTimeMs;
        });

        if (accepted.get()) return true;
        throw new ServiceException(CommonErrorCodeEnum.DUPLICATE_REQUEST_ERROR);
    }

    /**
     * 요청 완료 후 최소 중복 방지 시간을 다시 설정한다.
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        if (!(handler instanceof HandlerMethod handlerMethod)) return;

        Optional<DuplicateRequestPolicy> duplicateRequestPolicyOptional = getDuplicateRequestPolicy(handlerMethod);
        if (duplicateRequestPolicyOptional.isEmpty()) return;

        DuplicateRequestKey duplicateRequestKey = DuplicateRequestKey.from(request, handlerMethod);
        getDuplicateRequestExpireTimes(request).put(duplicateRequestKey, System.currentTimeMillis() + duplicateRequestPolicyOptional.get().minMs());
    }

    private Optional<DuplicateRequestPolicy> getDuplicateRequestPolicy(HandlerMethod handlerMethod) {
        return requestMappingAnnotationRegister
                .getAnnotationAttributes(handlerMethod, Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod.class)
                .map(DuplicateRequestPolicy::from);
    }

    @SuppressWarnings("unchecked")
    private Map<DuplicateRequestKey, Long> getDuplicateRequestExpireTimes(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object sessionAttribute = session.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION);
        if (sessionAttribute instanceof Map<?, ?> duplicateRequestExpireTimes) {
            return (Map<DuplicateRequestKey, Long>) duplicateRequestExpireTimes;
        }

        synchronized (session) {
            sessionAttribute = session.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION);
            if (sessionAttribute instanceof Map<?, ?> duplicateRequestExpireTimes) {
                return (Map<DuplicateRequestKey, Long>) duplicateRequestExpireTimes;
            }

            Map<DuplicateRequestKey, Long> duplicateRequestExpireTimes = new ConcurrentHashMap<>();
            session.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION, duplicateRequestExpireTimes);
            return duplicateRequestExpireTimes;
        }
    }

    private record DuplicateRequestKey(
            String handlerClassName,
            String handlerMethodName,
            String httpMethod,
            String requestUri,
            String queryString
    ) {
        private static DuplicateRequestKey from(HttpServletRequest request, HandlerMethod handlerMethod) {
            return new DuplicateRequestKey(
                    handlerMethod.getBeanType().getName(),
                    handlerMethod.getMethod().getName(),
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString()
            );
        }
    }

    private record DuplicateRequestPolicy(long maxMs, long minMs) {
        private static DuplicateRequestPolicy from(Map<String, Object> annotationAttributes) {
            return new DuplicateRequestPolicy(
                    ((Number) annotationAttributes.get("maxMs")).longValue(),
                    ((Number) annotationAttributes.get("minMs")).longValue()
            );
        }
    }
}
