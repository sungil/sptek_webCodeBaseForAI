package com._sptek.__webFramework.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Component

/**
 * Spring ApplicationContext, 현재 request/response/session, 환경 프로퍼티에 정적 접근을 제공하는 유틸리티.
 *
 * <p>Spring Component로 등록되어 ApplicationContext와 ObjectMapper를 주입받는다. 현재 요청 정보는
 * {@link RequestContextHolder}에 바인딩되어 있어야 하며, 이를 위해 {@code RequestContextListener} 또는
 * {@code RequestContextFilter} 등록이 필요하다.</p>
 */
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Getter
    private static ObjectMapper objectMapper;

    /**
     * 프레임워크 표준 ObjectMapper를 정적 유틸에서 사용할 수 있도록 보관한다.
     */
    public SpringUtil(@Qualifier("objectMapperWithXssProtectHelper") ObjectMapper objectMapper) {
        SpringUtil.objectMapper = objectMapper;
    }

    /**
     * Spring이 제공하는 ApplicationContext를 정적 보관소에 연결한다.
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    /**
     * ApplicationContext에서 {@link SpringBootConfiguration}이 붙은 메인 클래스를 찾는다.
     */
    public static Class<?> findMainClassFromContext(@Nullable ApplicationContext applicationContext) {
        ApplicationContext ctx = (applicationContext != null) ? applicationContext : SpringUtil.applicationContext;
        if (ctx == null) {
            throw new IllegalStateException("ApplicationContext is null");
        }

        BeanFactory bf = ctx.getAutowireCapableBeanFactory();
        if (!(bf instanceof ListableBeanFactory lbf)) {
            throw new IllegalStateException("BeanFactory is not a ListableBeanFactory");
        }

        Class<?> mainClass = resolveMainClass(lbf);
        //log.debug("main class is {}", mainClass.getName());
        return mainClass;
    }

    /**
     * ConditionContext의 BeanFactory에서 Spring Boot 메인 클래스를 찾는다.
     */
    public static Class<?> findMainClassFromContext(ConditionContext conditionContext) {
        BeanFactory bf = conditionContext.getBeanFactory();
        if (!(bf instanceof ListableBeanFactory lbf)) {
            throw new IllegalStateException("BeanFactory is not a ListableBeanFactory");
        }
        Class<?> mainClass = resolveMainClass(lbf);
        //log.debug("main class is {}", mainClass.getName());
        return mainClass;
    }

    /**
     * {@link SpringBootConfiguration} bean 타입을 찾고 CGLIB 프록시인 경우 실제 상위 클래스로 보정한다.
     */
    private static Class<?> resolveMainClass(ListableBeanFactory lbf) {
        String[] names = lbf.getBeanNamesForAnnotation(SpringBootConfiguration.class);
        if (names.length == 0) {
            throw new IllegalStateException("@SpringBootConfiguration class not found");
        }

        // 다중 후보일 경우 첫 번째 사용 (정책 필요 시 여기서 커스터마이즈)
        String beanName = names[0];
        Class<?> type = lbf.getType(beanName);
        if (type == null) {
            throw new IllegalStateException("Cannot resolve type for bean: " + beanName);
        }

        // CGLIB 프록시 대비: 상위 클래스에 실제 애노테이션이 있으면 승격
        Class<?> superClass = type.getSuperclass();
        if (superClass != null
                && superClass != Object.class
                && AnnotatedElementUtils.hasAnnotation(superClass, SpringBootConfiguration.class)) {
            return superClass;
        }
        return type;
    }

    /**
     * ApplicationContext에서 지정 타입의 Spring Bean을 조회한다.
     */
    public static <T> T getSpringBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    /**
     * 현재 스레드에 바인딩된 HttpServletRequest를 반환하거나 없으면 null을 반환한다.
     */
    public static @Nullable HttpServletRequest getRequestOrNull() {
        ServletRequestAttributes attrs = getServletRequestAttributesOrNull();
        return attrs != null ? attrs.getRequest() : null;
    }

    /**
     * 현재 스레드에 바인딩된 HttpServletRequest를 반환하고 없으면 예외를 던진다.
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = getRequestOrNull();
        if (request != null) {
            return request;
        }
        throw new IllegalStateException("No request bound to current thread");
    }

    /**
     * 현재 스레드에 바인딩된 HttpServletResponse를 반환하거나 없으면 null을 반환한다.
     */
    public static @Nullable HttpServletResponse getResponseOrNull() {
        ServletRequestAttributes attrs = getServletRequestAttributesOrNull();
        return attrs != null ? attrs.getResponse() : null;
    }

    /**
     * 현재 스레드에 바인딩된 HttpServletResponse를 반환하고 없으면 예외를 던진다.
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attrs = getServletRequestAttributesOrNull();
        if (attrs != null) {
            HttpServletResponse response = attrs.getResponse();
            if (response != null) {
                return response;
            }
            throw new IllegalStateException("No response available for current request");
        }
        throw new IllegalStateException("No request bound to current thread");
    }

    /**
     * 현재 request의 session을 반환하거나 요청 컨텍스트가 없으면 null을 반환한다.
     */
    public static @Nullable HttpSession getSessionOrNull(boolean create) {
        HttpServletRequest request = getRequestOrNull();
        return request != null ? request.getSession(create) : null;
    }

    /**
     * 현재 request의 session을 반환하고 요청 컨텍스트가 없으면 예외를 던진다.
     */
    public static HttpSession getSession(boolean create) {
        HttpServletRequest request = getRequestOrNull();
        if (request != null) {
            return request.getSession(create);
        }
        throw new IllegalStateException("No request bound to current thread");
    }

    /**
     * RequestContextHolder에서 Servlet request attribute를 안전하게 꺼낸다.
     */
    private static @Nullable ServletRequestAttributes getServletRequestAttributesOrNull() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes servletRequestAttributes ? servletRequestAttributes : null;
    }

    /**
     * Spring Environment에서 문자열 프로퍼티를 조회한다.
     */
    public static String getApplicationProperty(String key) {
        return getEnvironment().getProperty(key);
    }

    /**
     * Spring Environment에서 지정 타입으로 변환된 프로퍼티를 조회한다.
     */
    public static <T> T getApplicationProperty(String key, Class<T> targetType) {
        return getEnvironment().getProperty(key, targetType);
    }

    /**
     * ApplicationContext의 Environment를 반환한다.
     */
    private static Environment getEnvironment() {
        return Objects.requireNonNull(applicationContext, "ApplicationContext is null").getEnvironment();
    }

    /**
     * JOL을 사용해 객체 인스턴스 메모리 레이아웃 정보를 문자열로 반환한다.
     */
    public static String getInstanceMemoryInfo(Object object) {
        // implementation 'org.openjdk.jol:jol-core:0.17' 사용
        return object.getClass().getSimpleName() + " instance memory info\n" + ClassLayout.parseInstance(object).toPrintable();
    }
}
