package com._sptek._webFrameworkExample.aiExample.common.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiExampleSecureFilePathType {
    ANYONE("anyone"),
    LOGIN("login"),
    USER("user"),
    ROLE("role"),
    AUTHORITY("authority");

    private final String pathName;
}
