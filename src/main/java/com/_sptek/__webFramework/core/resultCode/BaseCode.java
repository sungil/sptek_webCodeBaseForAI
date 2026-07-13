package com._sptek.__webFramework.core.resultCode;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatusCode();
    String getResultCode();
    String getResultMessage();
}
