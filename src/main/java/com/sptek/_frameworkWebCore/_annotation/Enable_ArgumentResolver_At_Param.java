package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller 메서드의 파라미터에 붙여 특정 {@code HandlerMethodArgumentResolver} 적용 여부를 표시하는 마커 애노테이션.
 *
 * <p>이름의 {@code At_Param}은 이 애노테이션을 메서드나 클래스가 아니라 파라미터 위치에 적용한다는 뜻이다.
 * ArgumentResolver 구현체의 {@code supportsParameter}에서 파라미터 타입 조건과 함께 이 애노테이션을 확인하면,
 * 같은 DTO 타입이라도 명시적으로 표시한 파라미터에만 커스텀 바인딩을 적용할 수 있다.</p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_ArgumentResolver_At_Param {
}
