package com._sptek.__webFramework.event.listener.lifecycle.FrameworkContextRefreshed;

import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import com._sptek.__webFramework.bootstrap.startup.StartupEnvironmentLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Spring context refresh 완료 시 프레임워크 런타임 레지스트리를 초기화하는 listener.
 *
 * <p>메인 클래스 애노테이션, request mapping 애노테이션, 시작 환경 로그처럼
 * ApplicationContext 전체가 준비된 뒤 수집해야 하는 정보를 이 시점에 구성한다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ContextRefreshedEventListenerForFwResourceLoading {
    private final MainClassAnnotationRegister mainClassAnnotationRegister;
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;
    private final StartupEnvironmentLogger startupEnvironmentLogger;

    /**
     * context refresh 이벤트를 기준으로 프레임워크 레지스트리 초기화를 순서대로 수행한다.
     */
    @EventListener
    public void listen(ContextRefreshedEvent contextRefreshedEvent) throws Exception {
        log.debug("Event!");
        mainClassAnnotationRegister.initialize(contextRefreshedEvent.getApplicationContext());
        // 생성자 주입 순환을 피하면서도 프레임워크 의도대로 시작 시점에 RequestMapping 어노테이션 정보를 수집한다.
        requestMappingAnnotationRegister.initialize();
        startupEnvironmentLogger.logIfEnabled(contextRefreshedEvent.getApplicationContext());
    }
}

