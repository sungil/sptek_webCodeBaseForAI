package com.sptek.__webFramework.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * API 인가 실패를 HTTP 403으로 변환하는 JWT AccessDeniedHandler.
 *
 * <p>현재 기본 security chain에서는 CustomErrorController 흐름을 우선 사용하므로 직접 연결하지 않은 보조 구현이다.</p>
 */
@Slf4j
@Component
public class CustomJwtAccessDeniedHandlerForApi implements AccessDeniedHandler {
    //CustomErrorController 를 이용해서 Controller 외부 에러(필터쪽이나.. 기타 등등) 상황에 대한 처리를 하고 있어서 사용할 필요가 없음

    /**
     * 권한이 부족한 API 요청을 403 응답으로 종료한다.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error(accessDeniedException.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
