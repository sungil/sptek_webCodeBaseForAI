package com.sptek.__webFramework.web.asyncResponse;

import com.sptek.__webFramework.core.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.format.datetime.standard.DateTimeContextHolder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextTaskExecutor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAsync
@RequiredArgsConstructor

public class AsyncConfig {

    // 쓰레드 작업에 할용하기 위한 쓰레드 풀 생성
    @Bean(name = "baseTaskExecutor") // name값 변경 하지 말것!
    public ThreadPoolTaskExecutor baseTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(CommonConstants.RECOMMEND_THREAD_POOL_SIZE); // 기본 쓰레드 수
        threadPoolTaskExecutor.setMaxPoolSize(CommonConstants.RECOMMEND_THREAD_POOL_MAX_SIZE); // 최대 쓰레드 수
        threadPoolTaskExecutor.setQueueCapacity(CommonConstants.RECOMMEND_THREAD_QUEUE_SIZE); // 대기 큐 크기
        threadPoolTaskExecutor.setThreadNamePrefix("from threadPoolForAsync-");
        threadPoolTaskExecutor.setTaskDecorator(new CompositeContextTaskDecorator());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    // baseTaskExecutor 을 Spring 의 기본 쓰레드 풀로 적용하기 위해 Bean Name 을 기본값 으로 적용
    @Bean(name = "sptTaskExecutor")
    public TaskExecutor taskExecutor(@Qualifier("baseTaskExecutor") ThreadPoolTaskExecutor baseTaskExecutor) {
        // 하위 쓰레드에 SecurityContext 를 전파하기 위한 처리
        return new DelegatingSecurityContextTaskExecutor(baseTaskExecutor);
    }

    // todo: 중요! 하위 쓰레드 내에서도 ThreadLocal 기반의 중요 context 를 사용할 수 있도록 Decorator 설정
    public static class CompositeContextTaskDecorator implements TaskDecorator {
        @Override
        public @NotNull Runnable decorate(Runnable runnable) {
            var requestAttributes = RequestContextHolder.getRequestAttributes();
            var mdcContextMap = MDC.getCopyOfContextMap();
            var localeContext = LocaleContextHolder.getLocaleContext();
            var dateTimeContext = DateTimeContextHolder.getDateTimeContext();
            // (옵션) 커스텀 ThreadLocal 컨텍스트 캡처
            // var userContext = UserContext.getCurrentOrNull();

            return () -> {
                var prevRequestAttributes = RequestContextHolder.getRequestAttributes();
                var prevMdcContextMap = MDC.getCopyOfContextMap();
                var prevLocaleContext = LocaleContextHolder.getLocaleContext();
                var prevDateTimeContext = DateTimeContextHolder.getDateTimeContext();
                // var preUserContext = UserContext.getCurrentOrNull();

                try {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    if (mdcContextMap != null) MDC.setContextMap(mdcContextMap); else MDC.clear();
                    LocaleContextHolder.setLocaleContext(localeContext);
                    DateTimeContextHolder.setDateTimeContext(dateTimeContext);
                    // UserContext.setCurrent(userCtx);
                    //Thread 내용 실행 시점
                    runnable.run();

                } finally {
                    // 원래 값 복원 (복원 필시요 처리하면 됨)
                    MDC.clear();
                    if (prevMdcContextMap != null) MDC.setContextMap(prevMdcContextMap);
                    RequestContextHolder.resetRequestAttributes();
                    if (prevRequestAttributes != null) RequestContextHolder.setRequestAttributes(prevRequestAttributes);
                    LocaleContextHolder.setLocaleContext(prevLocaleContext);
                    DateTimeContextHolder.setDateTimeContext(prevDateTimeContext);
                    // UserContext.setCurrent(preUserContext);
                }
            };
        }
    }

    @Bean
    public BeanPostProcessor prioritizeMyHandler(AsyncControllerReturnValueHandler controllerReturnValueHandlerForAsync) {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                if (bean instanceof RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
                    List<HandlerMethodReturnValueHandler> existing = requestMappingHandlerAdapter.getReturnValueHandlers();
                    if (existing == null) return bean;

                    List<HandlerMethodReturnValueHandler> reordered = new ArrayList<>(existing);
                    // 중복 방지
                    reordered.removeIf(h -> h.getClass() == controllerReturnValueHandlerForAsync.getClass());
                    // 특정 케이스에 대해 Spring의 컨트롤러 핸들러 플루우를 타기전 Custom 플로우가 먼저 작동하게 하기 위해서
                    reordered.add(0, controllerReturnValueHandlerForAsync);
                    requestMappingHandlerAdapter.setReturnValueHandlers(reordered);
                }
                return bean;
            }
        };
    }
}
