package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * RestController 클래스에 붙여 API 성공 응답을 프레임워크 공통 응답 구조로 감싸도록 표시하는 애노테이션.
 *
 * <p>{@code ApiCommonResponseHelperAspect}가 이 애노테이션이 붙은 컨트롤러의 반환값을 공통 성공 응답 형태로 후처리한다.
 * 이미 공통 응답 객체를 직접 반환해야 하는 예외적인 API에는 적용 여부를 별도로 판단한다.</p>
 */
public @interface Enable_ResponseOfApiCommonSuccess_At_RestController {
}
