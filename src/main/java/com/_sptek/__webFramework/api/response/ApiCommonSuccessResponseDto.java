package com._sptek.__webFramework.api.response;

import com._sptek.__webFramework.core.code.BaseCode;
import com._sptek.__webFramework.core.code.SuccessCodeEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/*
//rest api 성공 응답 규격
HttpStatus.OK(200)
{
  "resultCode": "S000",
  "resultMessage": "success",
  "requestTime" : "2024-12-27T14:29:31.827941",
  "responseTime" : "2024-12-27T14:29:31.848168",
  "durationMsec" : "20",
  "result": { -->> T 객체에 선언된 내용으로 구성됨.
    "name": "myProject",
    "version": "v1",
    "description": "sptek web framework"
  }
}
 */
@Slf4j
@Getter
public class ApiCommonSuccessResponseDto<T> extends ApiBaseResponseDto {
    private final T result;

    public ApiCommonSuccessResponseDto(final T result) {
        super.makeTimestamp();
        super.resultCode = SuccessCodeEnum.DEFAULT_SUCCESS.getResultCode();
        super.resultMessage = SuccessCodeEnum.DEFAULT_SUCCESS.getResultMessage();
        this.result = result;
    }

    //성공 응답은 기본적으로 SuccessCodeEnum 안에서 선택함, SuccessCodeEnum에 따라 메시지지는 자동 결정, T는 "result" 필드에 들어가 object
    public ApiCommonSuccessResponseDto(final BaseCode successCodeEnum, final T result) {
        super.makeTimestamp();
        super.resultCode = successCodeEnum.getResultCode();
        super.resultMessage = successCodeEnum.getResultMessage();
        this.result = result;
    }

    //SuccessCodeEnum 안에서 선택할 수 없는 특별한 경우에만 사용.
    public ApiCommonSuccessResponseDto(final String resultCode, final String resultMessage, final T result) {
        super.makeTimestamp();
        super.resultCode = resultCode;
        super.resultMessage = resultMessage;
        this.result = result;
    }
}
