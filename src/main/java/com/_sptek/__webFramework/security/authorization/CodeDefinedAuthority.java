package com._sptek.__webFramework.security.authorization;

/**
 * 업무 코드에서 직접 선언하는 권한 enum이 프레임워크에 제공하는 권한 식별 계약.
 *
 * <p>실제 권한 목록은 업무 프로젝트에 두고, 프레임워크는 이 계약만 의존한다.
 * 컨트롤러/서비스의 {@code @PreAuthorize}나 SecurityFilterChain에서 enum name을 authority 문자열로 사용하고,
 * code/alias/description/status는 DB seed, 관리 화면, 검증 로직에서 참조하는 보조 식별자다.</p>
 */
public interface CodeDefinedAuthority {
    /**
     * Spring Security의 authority 문자열로 사용할 코드 정의 권한명.
     */
    String name();

    String getCode();
    String getAlias();
    String getDescription();
    String getStatus();
}
