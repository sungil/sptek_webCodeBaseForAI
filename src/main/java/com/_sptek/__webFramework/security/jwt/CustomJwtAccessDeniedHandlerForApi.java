package com._sptek.__webFramework.security.jwt;

import com._sptek.__webFramework.api.response.ApiCommonErrorResponseDto;
import com._sptek.__webFramework.core.resultCode.CommonErrorCodeEnum;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * API 인가 실패를 SPT 공통 API 에러 응답 형식의 HTTP 403으로 변환하는 JWT AccessDeniedHandler.
 *
 * <p>Security filter chain 내부의 인가 실패는 CustomErrorController로 forward되지 않을 수 있으므로,
 * 이 handler에서 공통 에러 DTO를 직접 직렬화한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CustomJwtAccessDeniedHandlerForApi implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    /**
     * 인증은 되었지만 권한이 부족한 API 요청을 403 공통 에러 응답으로 종료한다.
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        LoggingUtil.exLogging(log, accessDeniedException);
        ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.FORBIDDEN_ERROR, accessDeniedException.getMessage());

        response.setStatus(CommonErrorCodeEnum.FORBIDDEN_ERROR.getHttpStatusCode().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        LoggingUtil.reqResDetailLogging(log, request, response, apiCommonErrorResponseDto, "Req Res Detail Log From " + this.getClass().getSimpleName());
        objectMapper.writeValue(response.getWriter(), apiCommonErrorResponseDto);
    }
}
