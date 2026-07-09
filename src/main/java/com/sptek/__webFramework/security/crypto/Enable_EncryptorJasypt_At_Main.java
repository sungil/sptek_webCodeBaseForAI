package com.sptek.__webFramework.security.crypto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 Jasypt 문자열 암호화 Bean 구성을 활성화하는 애노테이션.
 *
 * <p>{@code JasyptStringEncryptorConfig}가 이 애노테이션을 조건으로 등록된다.
 * 암호화 키와 알고리즘 같은 실제 설정값은 환경별 설정에서 주입한다.</p>
 */
public @interface Enable_EncryptorJasypt_At_Main {
}
