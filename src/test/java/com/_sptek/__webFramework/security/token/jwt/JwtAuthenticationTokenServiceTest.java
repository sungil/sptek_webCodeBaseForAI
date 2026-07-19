package com._sptek.__webFramework.security.token.jwt;

import com._sptek.__webFramework.security.authentication.principal.FrameworkUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtAuthenticationTokenServiceTest {

    @Test
    void convertJwtToAuthenticationRestoresFrameworkUserDetailsPrincipal() {
        JwtAuthenticationTokenService tokenService = newTokenService();
        FrameworkUserDetails userDetails = FrameworkUserDetails.builder()
                .userId("1")
                .username("user@example.com")
                .displayName("User")
                .password("")
                .authorityNames(new LinkedHashSet<>(Set.of("ROLE_USER", "AUTH_TEST")))
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        Authentication restoredAuthentication = tokenService.convertJwtToAuthentication(tokenService.convertAuthenticationToJwt(authentication));

        assertThat(restoredAuthentication.getPrincipal()).isInstanceOf(FrameworkUserDetails.class);
        FrameworkUserDetails restoredPrincipal = (FrameworkUserDetails) restoredAuthentication.getPrincipal();
        assertThat(restoredPrincipal.getUserId()).isEqualTo("1");
        assertThat(restoredPrincipal.getUsername()).isEqualTo("user@example.com");
        assertThat(restoredPrincipal.getDisplayName()).isEqualTo("User");
        assertThat(restoredPrincipal.getAuthorityNames()).containsExactlyInAnyOrder("ROLE_USER", "AUTH_TEST");
    }

    @Test
    void convertJwtToAuthenticationRejectsMalformedToken() {
        JwtAuthenticationTokenService tokenService = newTokenService();

        assertThatThrownBy(() -> tokenService.convertJwtToAuthentication("not-a-jwt"))
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessageContaining("Malformed JWT token");
    }

    @Test
    void convertJwtToAuthenticationRejectsMissingSubjectClaim() {
        JwtAuthenticationTokenService tokenService = newTokenService();
        String token = Jwts.builder()
                .claim("auth", "ROLE_USER")
                .signWith(testKey(), SignatureAlgorithm.HS512)
                .setExpiration(validUntil())
                .compact();

        assertThatThrownBy(() -> tokenService.convertJwtToAuthentication(token))
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessageContaining("JWT subject claim is required");
    }

    @Test
    void convertJwtToAuthenticationRejectsMissingAuthorityClaim() {
        JwtAuthenticationTokenService tokenService = newTokenService();
        String token = Jwts.builder()
                .setSubject("1")
                .signWith(testKey(), SignatureAlgorithm.HS512)
                .setExpiration(validUntil())
                .compact();

        assertThatThrownBy(() -> tokenService.convertJwtToAuthentication(token))
                .isInstanceOf(JwtAuthenticationException.class)
                .hasMessageContaining("JWT authority claim is required");
    }

    @Test
    void validateJwtReturnsFalseWhenRequiredClaimIsMissing() {
        JwtAuthenticationTokenService tokenService = newTokenService();
        String token = Jwts.builder()
                .setSubject("1")
                .signWith(testKey(), SignatureAlgorithm.HS512)
                .setExpiration(validUntil())
                .compact();

        assertThat(tokenService.validateJwt(token)).isFalse();
    }

    @Test
    void propertiesRejectShortSecretKey() {
        JwtProperties properties = new JwtProperties();
        properties.setBase64SecretKey(Base64.getEncoder().encodeToString(new byte[32]));
        properties.setTokenValidityInMilliseconds(3600000);

        assertThatThrownBy(properties::validate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("jwt.base64SecretKey is too short for HS512 signing");
    }

    private JwtAuthenticationTokenService newTokenService() {
        JwtProperties properties = new JwtProperties();
        properties.setBase64SecretKey(testBase64SecretKey());
        properties.setTokenValidityInMilliseconds(3600000);
        JwtAuthenticationTokenService tokenService = new JwtAuthenticationTokenService(properties);
        tokenService.afterPropertiesSet();
        return tokenService;
    }

    private Key testKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(testBase64SecretKey()));
    }

    private String testBase64SecretKey() {
        byte[] key = new byte[64];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) i;
        }
        return Base64.getEncoder().encodeToString(key);
    }

    private Date validUntil() {
        return new Date(System.currentTimeMillis() + 3600000);
    }
}
