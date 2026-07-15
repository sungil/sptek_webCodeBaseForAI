package com._sptek.__webFramework.view.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Map;

/**
 * View 모델에 자동 주입할 properties 값이 실제로 설정된 경우에만 관련 Bean을 등록한다.
 */
public class HasViewModelAttributePropertiesCondition implements Condition {

    private static final String ATTRIBUTES_PROPERTY = "properties-for-model-attributes.attributes";

    /**
     * {@code properties-for-model-attributes.attributes} 설정이 비어 있지 않은지 확인한다.
     */
    @Override
    public boolean matches(@NotNull ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Binder.get(context.getEnvironment())
                .bind(ATTRIBUTES_PROPERTY, Bindable.mapOf(String.class, Object.class))
                .map(attributes -> !attributes.isEmpty())
                .orElse(false);
    }
}
