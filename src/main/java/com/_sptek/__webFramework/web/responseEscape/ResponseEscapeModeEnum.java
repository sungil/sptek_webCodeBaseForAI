package com._sptek.__webFramework.web.responseEscape;

/**
 * RestController 메서드가 선택한 JSON 응답 escape 정책.
 */
public enum ResponseEscapeModeEnum {
    HTML_ENTITY,
    JSON_UNICODE;

    static final String WRITER_ATTRIBUTE_NAME = ResponseEscapeModeEnum.class.getName();
}
