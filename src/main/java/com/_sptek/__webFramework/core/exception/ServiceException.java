package com._sptek.__webFramework.core.exception;

import com._sptek.__webFramework.core.code.BaseCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
서비스 로직상 더이상 추가 처리가 불가능 할 경우에 사용 한다.
exceptionMessage는 (주로 API 상황 에서) 사용자 에게 보여줄 얼럿 메시지 로 활용될 수 있다.
exceptionMessage를 입력하지 않으면 해당 serviceErrorCodeEnum 의 resultMessage로 자동 대체 된다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// todo : RuntimeException 과 Exception(Checked) 중 어느 것을 상속 해야 좋을지 고민이 있음..
public class ServiceException extends RuntimeException {
    private BaseCode serviceErrorCodeEnum;

    public ServiceException(BaseCode serviceErrorCodeEnum) {
        super(serviceErrorCodeEnum.getResultMessage());
        this.serviceErrorCodeEnum = serviceErrorCodeEnum;
    }

    public ServiceException(BaseCode serviceErrorCodeEnum, String exceptionMessage) {
        super(exceptionMessage);
        this.serviceErrorCodeEnum = serviceErrorCodeEnum;
    }
}
