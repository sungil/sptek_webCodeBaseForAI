package com.sptek.__webFramework.core.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

//요청이 실패일 경우 활용 되는 실패 코드 enum 객체
//http 상태 코드로 사용 되는  Http Status 와, response Body 에 들어 가는 resultCode, resultMessage 로 구성됨
//에러 DTO 를 통해 넘어 갈때는 exception Message 가 추가 되는데 이 exception Message 는 실제 에러 내용 으로 개발자 에게 정보 제공 측면 으로 활용 되는 것이고
//ErrorCode-Enum 의 result Message 는 일반 사용자 알림 활용 용도로 보면 좋을것 같다 (물론 result 코드를 이용해 처리할 수도 있음)

@Getter
@AllArgsConstructor
public enum CommonErrorCodeEnum implements BaseCode {
    //범용 적으로 사용 되고 있는 httpstatus 와 관련된 에러 (http status code 를 그에 맞게 내린다)
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "GE001", "BAD_REQUEST_ERROR"),
    INVALID_TYPE_VALUE_ERROR(HttpStatus.BAD_REQUEST, "GE002", "INVALID_TYPE_VALUE_ERROR"),
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "GE003", "MISSING_REQUEST_PARAMETER_ERROR"),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE004", "IO_ERROR"),
    REQUEST_BODY_NOT_READABLE_ERROR(HttpStatus.BAD_REQUEST, "GE005", "REQUEST_BODY_NOT_READABLE_ERROR"),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "GE006", "JSON_PARSE_ERROR"),
    JACKSON_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "GE007", "JACKSON_PROCESS_ERROR"),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "GE008", "FORBIDDEN_ERROR"),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "GE009", "NOT_FOUND_ERROR"),
    NULL_POINT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE010", "NULL_POINT_ERROR"),
    NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "GE011", "NOT_VALID_ERROR"),
    NOT_VALID_HEADER_ERROR(HttpStatus.BAD_REQUEST, "GE012", "NOT_VALID_HEADER_ERROR"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "GE013", "METHOD_NOT_ALLOWED"),
    PAYLOAD_EXCEEDED_ERROR(HttpStatus.PAYLOAD_TOO_LARGE, "GE014", "MAXIMUM UPLOAD SIZE EXCEEDED"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE999", "INTERNAL_SERVER_ERROR");


    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;

}
