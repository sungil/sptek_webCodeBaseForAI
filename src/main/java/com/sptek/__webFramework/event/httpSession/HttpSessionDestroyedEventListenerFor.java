package com.sptek.__webFramework.event.httpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 프레임워크 기본 세션 소멸 이벤트를 수신해 진단 로그를 남기는 listener.
 *
 * <p>{@link HttpSessionListenerAdapter}가 Servlet 세션 콜백을 Spring 이벤트로 변환한 뒤 이 listener가 수신한다.
 * 프로젝트별 후속 처리는 _projectCommon의 별도 listener에서 확장한다.</p>
 */
@Slf4j
@Component
@Configuration
public class HttpSessionDestroyedEventListenerFor {

    // Spring Security 설정에 따라 표준 HttpSessionDestroyedEvent 수신 조건이 달라질 수 있어 adapter event를 기본 경로로 사용한다.
    //@EventListener
    //public void listen(HttpSessionDestroyedEvent httpSessionDestroyedEvent) {
    //    log.debug("catched HttpSessionDestroyedEvent : sessionId({})", httpSessionDestroyedEvent.getSession().getId());
    //    //do more what you want..
    //}


    /**
     * adapter event에 포함된 세션 정보를 기준으로 세션 소멸 로그를 남긴다.
     */
    @EventListener
    public void listen(HttpSessionListenerAdapter.HttpSessionDestroyedEventAdapter httpSessionDestroyedEventAdapter) {
        log.debug("Event! : sessionId({})", httpSessionDestroyedEventAdapter.httpSessionEvent.getSession().getId());
        //do more what you want..
    }
}
