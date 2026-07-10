package com._sptek.__webFramework.data.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/**
 * 메인 클래스에 붙여 JPA와 MyBatis를 함께 사용하는 hybrid persistence 구성을 활성화하는 애노테이션.
 *
 * <p>{@code JpaHybridConfig}는 이 애노테이션이 있을 때 등록되고,
 * {@code MybatisConfig}의 일부 Bean은 이 애노테이션이 없을 때 등록되도록 분기된다.</p>
 */
public @interface Enable_JpaHybrid_At_Main {
}
