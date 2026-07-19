package com._sptek.__webFramework.security.crypto.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * DTO의 String 필드에 붙여 전역 암호화 유틸의 자동 복호화 대상임을 표시하는 애노테이션.
 *
 * <p>{@code EncryptorRegistry}가 reflection으로 DTO 필드를 순회할 때 이 애노테이션과 String 타입 여부를 함께 확인한다.
 * 암호화되지 않은 일반 문자열 필드에는 붙이지 않는다.</p>
 */
public @interface Enable_DecryptAuto_At_DtoString {
}
