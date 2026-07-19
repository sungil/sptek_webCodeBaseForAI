package com._sptek.__webFramework.security.token.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * JWT 검증 또는 필수 claim 해석 실패를 Spring Security 인증 실패로 전달하는 예외.
 *
 * <p>필터는 이 예외를 AuthenticationEntryPoint에 넘겨 기존 API 공통 401 응답 형식을 유지한다.
 * 내부 메시지는 로그와 테스트에서 실패 원인을 구분하기 위한 값이며, 운영 응답 노출 정책은 entry point에서 결정한다.</p>
 */
public class JwtAuthenticationException extends AuthenticationException {

    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
