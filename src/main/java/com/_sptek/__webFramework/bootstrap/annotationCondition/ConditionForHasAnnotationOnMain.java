package com._sptek.__webFramework.bootstrap.annotationCondition;

import com._sptek.__webFramework.core.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.Annotation;
import java.util.Map;

@Slf4j
/**
 * {@link HasAnnotationOnMain_At_Bean} 조건을 평가해 Bean 등록 여부를 결정하는 Spring Condition.
 *
 * <p>Spring Boot 메인 클래스에 특정 프레임워크 활성화 애노테이션이 붙어 있는지 확인한다.
 * 프레임워크 설정 Bean은 이 조건을 통해 메인 클래스의 {@code @Enable_*_At_Main} 선언과
 * 실제 Bean 등록 시점을 연결한다.</p>
 */
public class ConditionForHasAnnotationOnMain implements Condition {

    /**
     * 조건 애노테이션의 {@code value}와 {@code negate} 속성을 읽어 메인 클래스 애노테이션 보유 여부와 비교한다.
     */
    @Override
    public boolean matches(@NotNull ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(HasAnnotationOnMain_At_Bean.class.getName());
        if (attributes == null) return false;

        Class<?> annotationClass = (Class<?>) attributes.get("value");
        boolean negate = (boolean) attributes.get("negate");

        Class<?> mainClass = SpringUtil.findMainClassFromContext(context);
        boolean hasAnnotation = mainClass.isAnnotationPresent((Class<? extends Annotation>) annotationClass);
        return negate != hasAnnotation;
    }
}
