package com._sptek.__webFramework.security.token.jwt;

import com._sptek.__webFramework.api.response.ApiCommonErrorResponseDto;
import com._sptek.__webFramework.core.resultCode.CommonErrorCodeEnum;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * API 인증 실패를 SPT 공통 API 에러 응답 형식의 HTTP 401로 변환하는 JWT AuthenticationEntryPoint.
 *
 * <p>Security filter chain 내부의 인증 실패는 CustomErrorController로 forward되지 않을 수 있으므로,
 * 이 entry point에서 공통 에러 DTO를 직접 직렬화한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    /**
     * 인증 정보가 없거나 잘못된 API 요청을 401 공통 에러 응답으로 종료한다.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        LoggingUtil.exLogging(log, authException);
        ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.UNAUTHORIZED_ERROR, authException.getMessage());

        response.setStatus(CommonErrorCodeEnum.UNAUTHORIZED_ERROR.getHttpStatusCode().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        LoggingUtil.reqResDetailLogging(log, request, response, apiCommonErrorResponseDto, "Req Res Detail Log From " + this.getClass().getSimpleName());
        objectMapper.writeValue(response.getWriter(), apiCommonErrorResponseDto);
    }
}
