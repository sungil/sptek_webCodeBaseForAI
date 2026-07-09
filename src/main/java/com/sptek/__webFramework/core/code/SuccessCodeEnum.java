package com.sptek.__webFramework.core.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

//Rest API 요청이 성공일 경우 활용되는 성공코드 enum 객체
//http 상태코드로 사용되는  HttpStatus와, responseBody에 들어가는 resultCode, resultMessage 로 구성됨
@Getter
@AllArgsConstructor
public enum SuccessCodeEnum implements BaseCode {
    DEFAULT_SUCCESS(HttpStatus.OK, "S000", "success"),

    //아래 다른 sucessCode도 실제 쓸일이 있을까??
    SELECT_SUCCESS(HttpStatus.OK, "S001", "select success"),
    DELETE_SUCCESS(HttpStatus.OK, "S002", "delete success"),
    INSERT_SUCCESS(HttpStatus.OK, "S003", "insert success"),
    UPDATE_SUCCESS(HttpStatus.OK, "S004", "update success");

    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;
}
