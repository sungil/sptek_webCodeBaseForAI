package com.sptek._frameworkWebCore.event.listener.applicationEventListener.contextRefreshedEvent;

import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.SystemGlobalEnvTemporaryValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContextRefreshedEventListenerForFwResourceLoading {
    private final RequestMappingAnnotationRegister requestMappingAnnotationRegister;

    @EventListener
    public void listen(ContextRefreshedEvent contextRefreshedEvent) throws Exception {
        log.debug("Event!");
        new MainClassAnnotationRegister(contextRefreshedEvent.getApplicationContext());
        // 생성자 주입 순환을 피하면서도 프레임워크 의도대로 시작 시점에 RequestMapping 어노테이션 정보를 수집한다.
        requestMappingAnnotationRegister.initialize();
        new SystemGlobalEnvTemporaryValue(contextRefreshedEvent.getApplicationContext()); // NOTE: MainClassAnnotationRegister 보단 항상 뒤에 생성되야 함 (제약이 없도록 수정하면 좋을듯)
    }
}
