package com.sptek.__webFramework.bootstrap.testSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 애노테이션 탐지, Aspect pointcut, 인터셉터 조건 확인을 실험하기 위한 테스트용 마커 애노테이션.
 *
 * <p>모든 주요 Java 요소에 붙일 수 있도록 넓은 {@code @Target}을 가진다.
 * 실제 프레임워크 기능 스위치로 사용하지 않고 예제나 진단 코드에서만 사용한다.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE
})
public @interface TestAnnotation_At_All {

    /**
     * 테스트 시 임의 태그나 설명을 남기기 위한 값.
     */
    String value() default "";
}

