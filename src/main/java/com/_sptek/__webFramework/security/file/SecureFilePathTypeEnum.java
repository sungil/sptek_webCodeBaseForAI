package com._sptek.__webFramework.security.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 프레임워크 보안 파일 경로의 첫 번째 segment로 사용하는 접근 범위 코드.
 *
 * <p>{@code SecurityPathUtil}은 이 값을 기준으로 저장소 루트 property를 찾고,
 * 현재 인증 정보와 비교해 보안 파일 경로 접근 가능 여부를 판단한다.</p>
 */
@Getter
@RequiredArgsConstructor
public enum SecureFilePathTypeEnum {
    ANYONE("anyone"),
    LOGIN("login"),
    USER("user"),
    ROLE("role"),
    AUTH("auth");

    private final String pathName;
}
