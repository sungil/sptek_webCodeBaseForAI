package com._sptek._webFrameworkExample.unit.authentication.authorization;

import com._sptek.__webFramework.security.authorization.AuthorityDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 예제 업무 프로젝트의 인증/인가 화면과 기본 보안 데이터에서 사용하는 권한 코드 enum.
 *
 * <p>Spring Security의 authority 문자열로는 enum name을 사용하고,
 * code/alias/description은 DB 시드나 화면 표시, 관리 기능에서 참조할 수 있는 보조 식별자다.</p>
 */
@AllArgsConstructor
@Getter
public enum AuthorityEnum implements AuthorityDefinition {
    AUTH_SPECIAL_FOR_TEST("R000", "SFT", "테스트를 위해 만든 권한", ""),
    AUTH_RETRIEVE_USER_ALL_FOR_MARKETING("R001", "RUAFM", "모든 User에 대해서 마케팅에 필요한 정보를 조회할 수 있는 권한", ""),
    AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY("R002", "RUAFD", "모든 User에 대해서 배송에 필요한 정보를 조회할 수 있는 권한", "");

    private final String code;
    private final String alias;
    private final String description;
    private final String status;

    /**
     * 권한 code 값으로 enum을 역조회한다.
     */
    public static AuthorityEnum getAuthorityFromCode(String code) {
        return Arrays.stream(values())
                .filter(authorityEnum -> authorityEnum.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make AuthorityEnum from value. Unknown code: " + code));
    }

    /**
     * 권한 alias 값으로 enum을 역조회한다.
     */
    public static AuthorityEnum getAuthorityFromAlias(String alias) {
        return Arrays.stream(values())
                .filter(authorityEnum -> authorityEnum.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make AuthorityEnum from value. Unknown alias: " + alias));
    }

    /**
     * 권한 description 값으로 enum을 역조회한다.
     */
    public static AuthorityEnum getAuthorityFromDesc(String description) {
        return Arrays.stream(values())
                .filter(authorityEnum -> authorityEnum.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make AuthorityEnum from name. Unknown desc: " + description));
    }

//    // todo: 사용하기 편리하게 관련 const 변수를 만들어 줄까?
//    public static class SecuredPath {
//        final static String postSecured_Any_Auth = "postSecured-Any-Auth";
//        final static String putSecured_Any_Auth = "putSecured-Any-Auth";
//        final static String deleteSecured_Any_Auth = "deleteSecured-Any-Auth";
//        final static String secured_Any_Auth = "secured-Any-Auth";
//        final static String secured_Special_Auth = "secured-Special-Auth";
//        final static String secured_User_Role = "secured-User-Role";
//        final static String secured_system_Role = "secured-system-Role";
//        final static String secured_Admin_AdminSpecial_Role = "secured-Admin-AdminSpecial-Role";
//    }
}
