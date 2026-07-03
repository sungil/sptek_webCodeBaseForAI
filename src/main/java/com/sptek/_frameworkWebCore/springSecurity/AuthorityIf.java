package com.sptek._frameworkWebCore.springSecurity;

/**
 * 프레임워크 권한 코드가 공통으로 제공해야 하는 표시/식별 속성 규격.
 *
 * <p>enum 기반 권한 정의와 DB 저장용 권한 entity가 같은 code, alias, description, status 구조를 공유하게 한다.</p>
 */
public interface AuthorityIf {
    String getCode();
    String getAlias();
    String getDescription();
    String getStatus();
}
