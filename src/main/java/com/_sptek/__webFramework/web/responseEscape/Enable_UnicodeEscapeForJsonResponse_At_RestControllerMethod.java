package com._sptek.__webFramework.web.responseEscape;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * RestController 메서드의 JSON 응답에서 HTML 문맥상 위험한 문자를 Unicode escape로 쓰도록 표시하는 애노테이션.
 *
 * <p>{@code <}는 JSON 문서 안에서 {@code \u003C}로 표현되지만, JSON 파싱 후 값은 다시 {@code <}로 복원된다.
 * 데이터 의미를 바꾸는 기능이 아니라 JSON이 HTML/script 문맥에 잘못 삽입될 때의 위험을 줄이는 보조 방어다.</p>
 */
public @interface Enable_UnicodeEscapeForJsonResponse_At_RestControllerMethod {
}
