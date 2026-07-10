package com._sptek.__webFramework.bootstrap.annotationCondition;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(ConditionForHasAnnotationOnMain.class)
/**
 * Bean 클래스나 Bean 메서드에 붙여 메인 클래스의 특정 애노테이션 존재 여부를 조건으로 등록하는 애노테이션.
 *
 * <p>{@link ConditionForHasAnnotationOnMain}이 Spring Boot 메인 클래스를 찾아 {@link #value()} 애노테이션을 확인한다.
 * {@code @Enable_*_At_Main} 애노테이션으로 프레임워크 기능을 켜고 끄는 설정 Bean에서 사용한다.</p>
 */
public @interface HasAnnotationOnMain_At_Bean {

    /**
     * 메인 클래스에 존재해야 하는 애노테이션 타입.
     */
    Class<? extends Annotation> value();

    /**
     * true이면 지정한 애노테이션이 없을 때 Bean을 등록한다.
     */
    boolean negate() default false;
}
