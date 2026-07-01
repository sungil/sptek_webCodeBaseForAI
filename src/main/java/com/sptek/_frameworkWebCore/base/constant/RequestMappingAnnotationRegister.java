package com.sptek._frameworkWebCore.base.constant;

import com.sptek._frameworkWebCore.util.LoggingUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
// 각 RequestMapping 핸들러의 클래스와 메소드에 선언된 프레임워크 어노테이션 및 속성 정보를 HandlerMethod 기준으로 보관한다.
@Component
public class RequestMappingAnnotationRegister {

    private final ApplicationContext applicationContext;
    private volatile Map<HandlerMethodKey, Map<String, Map<String, Object>>> requestAnnotationRegister;

    // RequestMappingHandlerMapping 을 생성자에서 직접 주입하면 WebMvcConfigurer/Interceptor 생성과 순환 참조가 생길 수 있다.
    // 그래서 ApplicationContext 만 보관하고, ContextRefreshedEvent 시점에 MVC 매핑 Bean 이름으로 명시 조회한다.
    @Autowired
    public RequestMappingAnnotationRegister(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    RequestMappingAnnotationRegister(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        this.applicationContext = null;
        requestAnnotationRegister = buildRegister(requestMappingHandlerMapping);
    }

    public void initialize() {
        initializeIfNecessary();
    }

    private void initializeIfNecessary() {
        if (requestAnnotationRegister != null) {
            return;
        }
        synchronized (this) {
            if (requestAnnotationRegister != null) {
                return;
            }
            // Actuator 의 controllerEndpointHandlerMapping 도 같은 타입이므로, 타입 조회가 아니라 MVC 기본 Bean 이름으로 한정한다.
            RequestMappingHandlerMapping requestMappingHandlerMapping = Objects.requireNonNull(applicationContext)
                    .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
            requestAnnotationRegister = buildRegister(requestMappingHandlerMapping);
        }
    }

    private Map<HandlerMethodKey, Map<String, Map<String, Object>>> buildRegister(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        Map<HandlerMethodKey, Map<String, Map<String, Object>>> tempRequestAnnotationRegister = new HashMap<>();
        StringBuilder logBodyForNonSpecificMapping = new StringBuilder();

        requestMappingHandlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
            if (requestMappingInfo.getMethodsCondition().getMethods().isEmpty()) {
                logBodyForNonSpecificMapping.append(requestMappingInfo).append("\n");
            }

            tempRequestAnnotationRegister.put(
                    HandlerMethodKey.from(handlerMethod),
                    getAnnotationsWithAttributes(handlerMethod)
            );
        });

        Map<HandlerMethodKey, Map<String, Map<String, Object>>> register = Map.copyOf(tempRequestAnnotationRegister);
        log.info(LoggingUtil.makeBaseForm(CommonConstants.FW_START_LOG_TAG, "RequestMapping Annotation Register", register.toString()));
        log.info(LoggingUtil.makeBaseForm(CommonConstants.FW_START_LOG_TAG, "Not Specific HTTP Method (Not Recommended)", logBodyForNonSpecificMapping.isEmpty() ? "All Handlers are mapped with Specific HTTP Method (Good)" : logBodyForNonSpecificMapping.toString()));
        return register;
    }

    // 핸들러 메소드에서 어노테이션과 속성 정보를 가져옴
    private Map<String, Map<String, Object>> getAnnotationsWithAttributes(HandlerMethod handlerMethod) {
        Map<String, Map<String, Object>> annotationData = new HashMap<>();

        // 클래스에 달린 어노테이션 처리
        collectFrameworkAnnotations(handlerMethod.getBeanType(), annotationData);

        // 메소드에 달린 어노테이션 처리 (메소드 부분을 후 처리 함으로 메소드 적용된 내용이 최종 남게 됨, 메소드 적용이 우선순위가 높음으로..)
        collectFrameworkAnnotations(handlerMethod.getMethod(), annotationData);

        return Map.copyOf(annotationData);
    }

    private void collectFrameworkAnnotations(AnnotatedElement annotatedElement, Map<String, Map<String, Object>> annotationData) {
        MergedAnnotations.from(annotatedElement)
                .stream()
                .filter(mergedAnnotation -> isFrameworkAnnotation(mergedAnnotation.getType()))
                .forEach(mergedAnnotation -> annotationData.put(
                        mergedAnnotation.getType().getName(),
                        Map.copyOf(mergedAnnotation.asMap())
                ));
    }

    private boolean isFrameworkAnnotation(Class<? extends Annotation> annotationType) {
        return annotationType.getPackageName().startsWith(CommonConstants.FRAMEWORK_ANNOTATION_PACKAGE_NAME);
    }

    public boolean hasAnnotation(HttpServletRequest request, Class<? extends Annotation> annotation) {
        // URL 문자열을 재매칭하지 않고 Spring MVC 가 실제 선택한 HandlerMethod 기준으로 판단한다.
        // context-path, path variable, consumes/produces/params 조건 차이를 Spring 의 매칭 결과에 위임하기 위함이다.
        return resolveHandlerMethod(request)
                .map(handlerMethod -> hasAnnotation(handlerMethod, annotation))
                .orElse(false);
    }

    public boolean hasAnnotation(HandlerMethod handlerMethod, Class<? extends Annotation> annotation) {
        return getAnnotationAttributes(handlerMethod, annotation).isPresent();
    }

    public Optional<Map<String, Object>> getAnnotationAttributes(HttpServletRequest request, Class<? extends Annotation> annotation) {
        return resolveHandlerMethod(request)
                .flatMap(handlerMethod -> getAnnotationAttributes(handlerMethod, annotation));
    }

    public Optional<Map<String, Object>> getAnnotationAttributes(HandlerMethod handlerMethod, Class<? extends Annotation> annotation) {
        initializeIfNecessary();
        return Optional.ofNullable(requestAnnotationRegister.get(HandlerMethodKey.from(handlerMethod)))
                .map(annotations -> annotations.get(annotation.getName()));
    }

    public boolean hasRequestMapping(HttpServletRequest request) {
        return resolveHandlerMethod(request).isPresent();
    }

    public Optional<HandlerMethod> resolveHandlerMethod(HttpServletRequest request) {
        Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handler instanceof HandlerMethod handlerMethod) {
            return Optional.of(handlerMethod);
        }
        return Optional.empty();
    }

    private record HandlerMethodKey(Class<?> beanType, String methodName, List<Class<?>> parameterTypes) {
        private static HandlerMethodKey from(HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            // HandlerMethod 인스턴스 자체 대신 실제 사용자 클래스/시그니처를 key 로 사용해 프록시 차이를 줄인다.
            return new HandlerMethodKey(
                    ClassUtils.getUserClass(handlerMethod.getBeanType()),
                    method.getName(),
                    List.of(method.getParameterTypes())
            );
        }
    }
}
