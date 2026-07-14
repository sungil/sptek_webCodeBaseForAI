package com._sptek.__webFramework.bootstrap.registry;

import com._sptek.__webFramework.core.constant.WebFrameworkPackageConstants;
import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.event.listener.lifecycle.FrameworkContextRefreshed.ContextRefreshedEventListenerForFwResourceLoading;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import com._sptek.__webFramework.core.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring Boot 메인 클래스에 선언된 프레임워크 {@code @Enable_*_At_Main} 애노테이션 정보를 수집하는 런타임 레지스트리.
 *
 * <p>프레임워크 기능 중 Bean 등록 시점 이후에도 메인 클래스의 활성화 애노테이션과 속성값을 반복 조회해야 하는 코드가
 * 이 레지스트리를 사용한다. {@link ContextRefreshedEventListenerForFwResourceLoading}에서 ApplicationContext 준비 후
 * 한 번 초기화되며, 이후 필터/인터셉터/유틸리티가 static 조회 메서드로 참조한다.</p>
 *
 * <p>현재 구현은 단일 Spring ApplicationContext를 전제로 한 static 저장소다. 테스트나 멀티 컨텍스트 환경에서는
 * 이전 컨텍스트의 값이 남을 수 있으므로, 프레임워크 초기화 순서와 컨텍스트 수명 주기를 함께 고려해야 한다.</p>
 */
@Slf4j
@Component
public class MainClassAnnotationRegister {
    private static Map<String, Map<String, Object>> mainClassAnnotationRegister = Collections.emptyMap();

    /**
     * ApplicationContext 준비 후 메인 클래스의 프레임워크 애노테이션 정보를 static 조회 저장소에 적재한다.
     */
    public void initialize(ApplicationContext applicationContext) throws Exception {
        synchronized (MainClassAnnotationRegister.class) {
            if (!mainClassAnnotationRegister.isEmpty()) return;

            Class<?> mainClass = SpringUtil.findMainClassFromContext(applicationContext);
            Map<String, Map<String, Object>> temp = new HashMap<>();
            for (Annotation annotation : mainClass.getAnnotations()) {
                String name = annotation.annotationType().getName();
                if (name.startsWith(WebFrameworkPackageConstants.WEB_FRAMEWORK_ANNOTATION_PACKAGE_PREFIX)) {
                    Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(annotation, false);
                    temp.put(name, Map.copyOf(attrs));
                }
            }
            mainClassAnnotationRegister = Map.copyOf(temp);
            log.info(LoggingUtil.makeBaseForm(LoggingConstants.FW_START_LOG_TAG, "MainClass Annotation Register", makeLogBody(mainClassAnnotationRegister)));
        }
    }

    private String makeLogBody(Map<String, Map<String, Object>> annotationRegister) {
        return annotationRegister.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> "- %s %s".formatted(
                        entry.getKey().replace(WebFrameworkPackageConstants.WEB_FRAMEWORK_ANNOTATION_PACKAGE_PREFIX, ""),
                        entry.getValue().isEmpty() ? "" : entry.getValue()
                ).stripTrailing())
                .collect(Collectors.joining("\n"));
    }

    public static boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return mainClassAnnotationRegister.containsKey(annotation.getName());
    }

    public static Map<String, Object> getAnnotationAttributes(Class<? extends Annotation> annotation) {
        return mainClassAnnotationRegister.getOrDefault(annotation.getName(), Map.of());
    }
}

