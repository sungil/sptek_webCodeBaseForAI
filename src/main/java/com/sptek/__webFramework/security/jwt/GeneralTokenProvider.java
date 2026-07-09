package com.sptek.__webFramework.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Spring Security Authentication과 JWT 문자열 사이의 변환 및 검증을 담당하는 provider.
 *
 * <p>JWT payload에는 subject와 authority 문자열만 담고, 위변조 검증은 설정에서 주입된 HMAC secret key로 수행한다.
 * JWT는 암호화가 아니라 서명된 Base64 구조이므로 민감 정보를 넣지 않는다.</p>
 */
@Slf4j
@Component
public class GeneralTokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private final String secretKey;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public GeneralTokenProvider(@Value("${jwt.base64SecretKey}") String secretKey, @Value("${jwt.tokenValidityInMilliseconds}") long tokenValidityInMilliseconds) {
        this.secretKey = secretKey;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    /**
     * Base64 secret key를 JWT 서명용 Key 객체로 변환한다.
     */
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 인증 객체의 name과 authorities를 JWT subject/claim으로 변환해 서명된 token을 생성한다.
     */
    public String convertAuthenticationToJwt(Authentication authentication){
        log.debug("origin authentication: {}", authentication);
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 토큰 만료 시간 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
        /*
        todo:
        최종 토큰 형태 : eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdW5naWxyeTFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTc1MjEzNzUxNn0.qfb5BBjZktqcif9XBCSDpk2okoYj5qO19qUGSaM1xjSF9hc-xylgjvGtgkbzd9XmpD5-zi6PdlmghyTW8EZ9xw
        토큰은 암호화 된것이 아니라 Base64 인코딩된 상태로 그런 이유로 주요 정보는 포함하지 않아야 함.
        그러한 이유로 브라우저에서 직접 로그인하고 sessionId로 인증 처리가 되는 케이스와(view) 와 Authorization: Bearer 토큰으로 인증받은 케이스의 SecurityContextHolder 에서 Authentication 을 가져왔을때 서로 정보의 량이 틀릴수 있음을 꼭 알아야 함!)
        단지 시그니쳐 값을 통해서 위변조 여부를 판단할 뿐이다. (서버로 토큰이 들어오면 본문 필드와 시그니쳐 시크릿 값으로 서명했을때 동일하면 유효로 판담)
        <토큰 구조>
            eyJhbGciOiJIUzUxMiJ9 ← Header (Base64 인코딩).
            eyJzdWIiOiJzdW5naWxyeTFAbmF2ZXIuY29tIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTc1MjEzNzUxNn0  ← Payload (Base64 인코딩).
            qfb5BBjZktqcif9XBCSDpk2okoYj5qO19qUGSaM1xjSF9hc-xylgjvGtgkbzd9XmpD5-zi6PdlmghyTW8EZ9xw ← Signature
         */
    }

    /**
     * JWT claims의 subject와 authority claim으로 Spring Security Authentication을 재구성한다.
     */
    public Authentication convertJwtToAuthentication(String token){
        // 토큰을 이용하여 claim 생성
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // claim을 이용하여 authorities 생성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * JWT 서명, 만료, 지원 여부를 검증한다.
     */
    public boolean validateJwt(String jwt){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) { log.error("Invalid JWT signature"); }
        catch (ExpiredJwtException e) { log.error("Expired JWT token"); }
        catch (UnsupportedJwtException e) { log.error("Unsupported JWT token"); }
        catch (IllegalArgumentException e) { log.error("Invalid JWT token"); }

        return false;
    }
}
