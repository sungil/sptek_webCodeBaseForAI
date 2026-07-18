package com._sptek.__webFramework.security.authentication.principal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 세션 로그인과 JWT 인증에서 공통으로 사용하는 프레임워크 인증 사용자 principal.
 *
 * <p>프레임워크는 업무 사용자 entity/DTO를 직접 알지 않고, 이 principal의 userId, username,
 * displayName, authority 문자열만 기준으로 현재 인증 사용자를 해석한다.</p>
 *
 * <p>Spring Security에서 principal은 Authentication 안에 들어가는 "현재 사용자 본체"다.
 * 세션 로그인은 UserDetailsService가 이 객체를 만들고, JWT 인증은 token claim을 해석해 같은 객체를 복원한다.</p>
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrameworkAuthenticatedUser implements UserDetails {
    /**
     * 업무 사용자 PK를 문자열로 보관한다.
     *
     * <p>JWT subject에도 같은 값을 넣어 세션 인증과 JWT 인증의 사용자 식별 기준을 맞춘다.</p>
     */
    private String userId;

    /**
     * Spring Security의 username에 해당하는 로그인 식별자.
     *
     * <p>현재 예제 업무에서는 email을 username으로 사용한다.</p>
     */
    private String username;

    /**
     * 화면, 로그, 모델 등에 노출할 수 있는 사용자 표시 이름.
     */
    private String displayName;

    /**
     * form login password 검증에 사용하는 encoded password.
     *
     * <p>JWT에서 복원한 principal은 서버가 password를 다시 검증하지 않으므로 빈 문자열을 사용한다.</p>
     */
    private String password;

    @Builder.Default
    private Set<String> authorityNames = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
