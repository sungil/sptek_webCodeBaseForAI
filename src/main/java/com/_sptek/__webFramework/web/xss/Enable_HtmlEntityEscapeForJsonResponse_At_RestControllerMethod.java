package com._sptek.__webFramework.web.xss;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * RestController 메서드의 JSON 응답 문자열 값을 HTML entity로 escape 하도록 표시하는 애노테이션.
 *
 * <p>JSON 파싱 후 클라이언트가 받는 값도 {@code &lt;} 같은 표시용 문자열로 바뀐다.
 * 레거시 화면에서 응답 값을 {@code innerHTML} 등에 직접 넣는 것처럼 클라이언트 출력 처리를 신뢰하기 어려운
 * 제한적인 경우에만 사용한다.</p>
 */
public @interface Enable_HtmlEntityEscapeForJsonResponse_At_RestControllerMethod {
}
