package com.sptek.__webFramework.api.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * RestController 클래스에 붙여 API 예외 응답을 프레임워크 공통 오류 구조로 처리하도록 표시하는 애노테이션.
 *
 * <p>API 전역 예외 처리기가 요청 매핑 애노테이션 정보를 확인해 이 애노테이션이 적용된 API의 오류 응답을
 * JSON 기반 공통 응답 포맷으로 변환한다.</p>
 */
public @interface Enable_ResponseOfApiGlobalException_At_RestController {
}
