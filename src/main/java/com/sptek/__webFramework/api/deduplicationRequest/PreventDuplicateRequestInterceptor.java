package com.sptek.__webFramework.api.deduplicationRequest;

import com.sptek.__webFramework.core.constant.CommonConstants;
import com.sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import com.sptek.__webFramework.core.exception.ServiceException;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 중복 요청 방지 애노테이션이 붙은 API HandlerMethod에 세션 단위 de-duplication을 적용하는 interceptor.
 *
 * <p>URL 재매칭이 아니라 {@link RequestMappingAnnotationRegister}가 수집한 HandlerMethod 애노테이션을 기준으로 판단한다.
 * 같은 세션의 연속 요청은 짧은 시간 창 안에서 ServiceException으로 차단한다.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor

public class PreventDuplicateRequestInterceptor implements HandlerInterceptor {
    // 사용자(SessionId)별 Syncronized 한 작업이 필요할때 lock 객체로 사용 할수 있다
    private final ConcurrentHashMap<String, ReentrantLock> SESSION_LOCK = new ConcurrentHashMap<>();
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;

    /**
     * 대상 HandlerMethod이면 세션별 lock과 만료 시각을 확인해 중복 요청을 차단한다.
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        // 중복 요청 방지는 실제 실행될 컨트롤러 메소드의 어노테이션 기준으로만 적용한다.
        // URL 패턴을 다시 추정하지 않아 같은 path 의 consumes/params 분기에서도 오동작하지 않는다.
        if (!(handler instanceof HandlerMethod handlerMethod)
                || !requestMappingAnnotationRegister.hasAnnotation(handlerMethod, Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod.class)) return true;
        // NOTE : (중요) SESSION_LOCK 의 키값이 세션 id 이기 때문에 만약 다른 페이지를 거치지 않고 세션이 없는 상태에서 해당 컨트롤러로 최초 진입하는 케이스라면 중복 허용이 될수 있음 (현실적으로 그런 케이스는 거의 없음, 테스트를 위해 세션을 날리고 바로 req 하는 경우 주의)
        ReentrantLock reentrantLock = SESSION_LOCK.computeIfAbsent(request.getSession().getId(), k -> new ReentrantLock());
        try {
            reentrantLock.lock();
            long deDuplicateExpiredTimeMs = request.getSession().getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION) == null ?
                    0L : (long) request.getSession().getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION);

            if (deDuplicateExpiredTimeMs < System.currentTimeMillis()) {
                request.getSession().setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION, System.currentTimeMillis() + CommonConstants.DUPLICATION_PREVENT_MAX_MS);
                // todo: (중요) Async Response 에서는 여기서 true 를 리턴해도 첫번째 호출에서는 실제 컨트롤러를 진입을 하지 않는다. (그래서 아래 두번째 호출때 처리함)
                return true;

            } else {
                // todo: (중요) 유효한 deDuplicateExpiredTimeMs 이 있다 하더라도 이번 요청이 Async Response 두 번째 호출이라면 이번 요청이 실제 처리 요청 임으로 진행시킨다.
                if (request.getDispatcherType() == DispatcherType.ASYNC) return true;
                throw new ServiceException(ServiceErrorCodeEnum.DUPLICATION_REQUEST_ERROR);
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    /**
     * 요청 완료 후 최소 중복 방지 시간을 다시 설정한다.
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws InterruptedException {
        ReentrantLock reentrantLock = SESSION_LOCK.computeIfAbsent(request.getSession().getId(), k -> new ReentrantLock());
        try {
            reentrantLock.lock();
            request.getSession().setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION, System.currentTimeMillis() + CommonConstants.DUPLICATION_PREVENT_MIN_MS);
        } finally {
            reentrantLock.unlock();
        }
    }
}
