package com._sptek.__webFramework.view.error;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * View Controller 클래스에 붙여 화면 요청의 예외를 프레임워크 공통 오류 페이지 흐름으로 처리하도록 표시하는 애노테이션.
 *
 * <p>View 전역 예외 처리 흐름은 요청 매핑 애노테이션 정보를 기준으로 이 표시가 있는 컨트롤러를 구분한다.
 * API JSON 응답용 컨트롤러에는 {@code Enable_ResponseOfApiGlobalException_At_RestController}를 사용한다.</p>
 */
public @interface Enable_ResponseOfViewGlobalException_At_ViewController {
}
