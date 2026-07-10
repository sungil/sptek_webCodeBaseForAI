package com._sptek.__webFramework.security.util;

import com._sptek.__webFramework.core.constant.CommonConstants;
import com._sptek.__webFramework.security.userStore.dto.UserDto;
import com._sptek.__webFramework.security.authentication.userStore.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
/**
 * Spring SecurityContextHolder에서 현재 인증 사용자, 사용자 DTO, 역할/권한 정보를 추출하는 유틸리티.
 */
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
            return !CommonConstants.ANONYMOUS_USER.equals(AuthenticationUtil.getMyAuthentication().getPrincipal().toString());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 현재 인증 principal에서 프레임워크 사용자 DTO를 추출한다.
     */
    public static Optional<UserDto> getMyUserDtoOptional() {
        if (!isRealLogin()) return Optional.empty();
        try {
            return Optional.ofNullable(((CustomUserDetails) AuthenticationUtil.getMyAuthentication().getPrincipal()).getUserDto());
        } catch (ClassCastException e) {
            // todo: ClassCastException 하는 이유는 sessionId로(view) 인증 받는 케이스와 JWT로 인증 받는 케이스에 SecurityContextHolder 의 Authentication 정보 구조가 서로 다르기 때문임
            return Optional.empty();
        }
    }

    /**
     * 현재 사용자 DTO를 반환하거나 없으면 null을 반환한다.
     */
    public @Nullable static UserDto getMyUserDto() {
        return getMyUserDtoOptional().orElse(null);
    }

    /**
     * 현재 사용자 PK를 Optional로 반환한다.
     */
    public static Optional<Long> getMyIdOptional() {
        return getMyUserDtoOptional().map(UserDto::getId);
    }

    /**
     * 현재 사용자 PK를 반환하거나 없으면 null을 반환한다.
     */
    public @Nullable static Long getMyId() {
        return getMyIdOptional().orElse(null);
    }

    /**
     * 현재 사용자 표시 이름으로 email 또는 UserDetails username을 반환한다.
     */
    public static String getMyName() {
        Authentication authentication = AuthenticationUtil.getMyAuthentication();
        if (!isRealLogin() || authentication == null) return CommonConstants.ANONYMOUS_USER;

        return getMyUserDtoOptional()
                .map(UserDto::getEmail)
                .orElseGet(() -> authentication.getPrincipal() instanceof UserDetails userDetails
                        ? userDetails.getUsername()
                        : CommonConstants.ANONYMOUS_USER);
    }

    /**
     * 현재 사용자 email을 Optional로 반환한다.
     */
    public static Optional<String> getMyEmailOptional() {
        return getMyUserDtoOptional().map(UserDto::getEmail);
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
}
