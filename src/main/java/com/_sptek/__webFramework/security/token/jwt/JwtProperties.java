package com._sptek.__webFramework.security.token.jwt;

import io.jsonwebtoken.io.Decoders;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * JWT 발급/검증에 필요한 업무 프로젝트 설정을 바인딩한다.
 *
 * <p>현재 provider는 HS512 서명을 사용하므로 Base64 secret은 디코딩 후 64 byte 이상이어야 한다.
 * JWT는 암호화가 아니라 서명이므로 secret은 외부 환경 변수나 secret manager에서 관리한다.</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String base64SecretKey;
    private long tokenValidityInMilliseconds;

    /**
     * JWT 설정이 provider가 사용할 수 있는 값인지 서버 시작 시점에 검증한다.
     */
    public void validate() {
        if (!StringUtils.hasText(base64SecretKey)) {
            throw new IllegalStateException("jwt.base64SecretKey is required");
        }
        if (tokenValidityInMilliseconds <= 0) {
            throw new IllegalStateException("jwt.tokenValidityInMilliseconds must be greater than 0");
        }

        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(base64SecretKey);
        } catch (RuntimeException e) {
            throw new IllegalStateException("jwt.base64SecretKey must be Base64 encoded", e);
        }

        if (keyBytes.length < 64) {
            throw new IllegalStateException("jwt.base64SecretKey is too short for HS512 signing");
        }
    }
}
