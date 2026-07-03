package com.sptek._frameworkWebCore.filter.deprecated;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Aspect 기반 method usage 추적 결과를 필터에서 출력하려던 deprecated 필터.
 *
 * <p>현재 등록 애노테이션과 내부 처리 코드가 비활성화되어 런타임 기능으로 사용되지 않는다.
 * 신규 호출 추적은 logging/event의 활성 구조를 우선 검토한다.</p>
 */
@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@WebFilter(urlPatterns = "/*")
public class MethodeUsageAuditFilter extends OncePerRequestFilter {

    public MethodeUsageAuditFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * 현재는 method usage audit 처리 없이 종료되는 deprecated hook이다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull jakarta.servlet.FilterChain filterChain) throws ServletException, IOException {
//
//        // 요청 처리 전
//        LoggingAspect.clearCallStack();
//
//        // 체인 다음 필터로 요청 전달
//        filterChain.doFilter(request, response);
//
//        // 응답 처리 후
//        List<String> callStack = LoggingAspect.getCallStack();
//        StringBuilder filteredCallStack = new StringBuilder("Filtered Call Stack:\n");
//        callStack.forEach(methodName -> filteredCallStack.append(methodName).append("\n"));
//
//        // 로깅
//        log.debug(filteredCallStack.toString());
//
//        // Clear call stack after logging
//        LoggingAspect.clearCallStack();
    }
}
