package com._sptek.__webFramework.core.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatusCode();
    String getResultCode();
    String getResultMessage();
}
