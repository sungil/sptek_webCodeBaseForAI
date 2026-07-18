package com._sptek.__webFramework.security.util;

import com._sptek.__webFramework.security.SecurityConstants;
import com._sptek.__webFramework.security.authentication.principal.FrameworkAuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Spring SecurityContextHolder에서 현재 인증 사용자, 역할/권한 정보를 추출하는 유틸리티.
 *
 * <p>프레임워크는 업무 사용자 DTO를 직접 알지 않고 {@link FrameworkAuthenticatedUser} principal만 해석한다.
 * 세션 로그인과 JWT 인증 모두 이 principal을 사용하면 사용자 식별자 조회 결과가 동일하게 유지된다.</p>
 *
 * <p>SecurityContextHolder는 현재 요청 thread에 바인딩된 SecurityContext를 제공한다.
 * 비로그인 요청도 AnonymousAuthenticationFilter 때문에 Authentication이 존재할 수 있으므로,
 * 현재 사용자 조회 전에는 {@link #isRealLogin()} 기준으로 anonymous 사용자를 제외한다.</p>
 */
@Slf4j
public class AuthenticationUtil {

    /**
     * Spring Security 필터가 구성한 현재 Authentication 전체 정보를 반환한다.
     */
    public static Authentication getMyAuthentication() {
        //log.debug("getMyAuthentication : {}", SecurityContextHolder.getContext().getAuthentication());
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * anonymousUser가 아닌 실제 로그인 사용자인지 확인한다.
     *
     * <p>Spring Security 기본 동작상 비로그인 요청도 authenticated anonymous로 보일 수 있어
     * principal 값을 기준으로 한 번 더 구분한다.</p>
     */
    public static boolean isRealLogin() {
        // spring security 가 다 로딩 되기 전의 호출이나 filter chain 이 적용 되지 않는 request 등을 위한 방어
        try {
            if(AuthenticationUtil.getMyAuthentication() == null)
                return false;
            return !SecurityConstants.ANONYMOUS_USER.equals(AuthenticationUtil.getMyAuthentication().getPrincipal().toString());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 현재 인증 principal에서 프레임워크 공통 인증 사용자를 추출한다.
     *
     * <p>세션 로그인과 JWT 인증이 모두 FrameworkAuthenticatedUser를 principal로 사용해야 Optional에 값이 들어간다.
     * Spring Security 기본 principal이나 anonymous principal은 업무 사용자로 해석하지 않는다.</p>
     */
    public static Optional<FrameworkAuthenticatedUser> getMyPrincipalOptional() {
        if (!isRealLogin()) return Optional.empty();

        Object principal = AuthenticationUtil.getMyAuthentication().getPrincipal();
        return principal instanceof FrameworkAuthenticatedUser frameworkAuthenticatedUser
                ? Optional.of(frameworkAuthenticatedUser)
                : Optional.empty();
    }

    /**
     * 현재 인증 principal을 반환하거나 없으면 null을 반환한다.
     */
    public @Nullable static FrameworkAuthenticatedUser getMyPrincipal() {
        return getMyPrincipalOptional().orElse(null);
    }

    /**
     * 현재 사용자 PK를 Optional로 반환한다.
     *
     * <p>JWT subject와 세션 principal의 userId는 문자열로 보관하므로, 기존 Long 기반 호출자를 위해 숫자 변환을 시도한다.
     * 이전 방식으로 발급된 token처럼 subject가 email이면 값이 비어 있을 수 있다.</p>
     */
    public static Optional<Long> getMyIdOptional() {
        return getMyPrincipalOptional()
                .map(FrameworkAuthenticatedUser::getUserId)
                .flatMap(AuthenticationUtil::parseLong);
    }

    /**
     * 현재 사용자 PK를 반환하거나 없으면 null을 반환한다.
     */
    public @Nullable static Long getMyId() {
        return getMyIdOptional().orElse(null);
    }

    /**
     * 현재 사용자 식별자를 문자열 Optional로 반환한다.
     */
    public static Optional<String> getMyUserIdOptional() {
        return getMyPrincipalOptional().map(FrameworkAuthenticatedUser::getUserId);
    }

    /**
     * 현재 사용자 식별자를 문자열로 반환하거나 없으면 null을 반환한다.
     */
    public @Nullable static String getMyUserId() {
        return getMyUserIdOptional().orElse(null);
    }

    /**
     * 현재 사용자 표시 이름을 반환한다.
     */
    public static String getMyName() {
        Authentication authentication = AuthenticationUtil.getMyAuthentication();
        if (!isRealLogin() || authentication == null) return SecurityConstants.ANONYMOUS_USER;

        return getMyPrincipalOptional()
                .map(FrameworkAuthenticatedUser::getDisplayName)
                .filter(displayName -> displayName != null && !displayName.isBlank())
                .orElseGet(() -> authentication.getPrincipal() instanceof UserDetails userDetails
                        ? userDetails.getUsername()
                        : SecurityConstants.ANONYMOUS_USER);
    }

    /**
     * 현재 사용자 email을 Optional로 반환한다.
     */
    public static Optional<String> getMyEmailOptional() {
        return getMyPrincipalOptional().map(FrameworkAuthenticatedUser::getUsername);
    }

    /**
     * 현재 사용자 email을 반환하거나 없으면 null을 반환한다.
     */
    public @Nullable static String getMyEmail() {
        return getMyEmailOptional().orElse(null);
    }

    /**
     * 현재 사용자 GrantedAuthority 중 ROLE_ prefix를 가진 값을 반환한다.
     */
    public static Set<String> getMyRoles() {
        return getMyAuthorities("ROLE_");
    }

    /**
     * 현재 사용자 GrantedAuthority 중 AUTH_ prefix를 가진 값을 반환한다.
     */
    public static Set<String> getMyAuths() {
        return getMyAuthorities("AUTH_");
    }

    /**
     * 현재 사용자 권한 중 지정 prefix로 시작하는 값을 중복 없이 반환한다.
     */
    private static Set<String> getMyAuthorities(String authFilterStr) {
        Authentication authentication = AuthenticationUtil.getMyAuthentication();
        if (!isRealLogin() || authentication == null) return Set.of();

        Set<String> uniqueGrantedAuthorities = new HashSet<>();
        authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(grantedAuthority -> grantedAuthority != null && grantedAuthority.startsWith(authFilterStr))
                // ROLE_, AUTH_ prefix 를 제외 할지 여부
                // .forEach(grantedAuthority -> uniqueGrantedAuthorities.add(grantedAuthority.substring(authFilterStr.length())));
                .forEach(uniqueGrantedAuthorities::add);

        //log.debug("getMy{} : {}", authFilterStr, uniqueGrantedAuthorities);
        return uniqueGrantedAuthorities;
    }

    private static Optional<Long> parseLong(String value) {
        try {
            return Optional.ofNullable(value).map(Long::valueOf);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
