package com.sptek._projectsCommon.commonObject.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SecureFilePathTypeEnum {
    ANYONE("anyone"),
    LOGIN("login"),
    USER("user"),
    ROLE("role"),
    AUTH("auth");

    private final String pathName;
}
