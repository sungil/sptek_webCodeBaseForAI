package com.sptek._projectsCommon.event.listener.applicationEventListener.contextRefreshedEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextRefreshedEventListenerForExample {

    // 애플리케이션 컨텍스트가 초기화되거나 새로고침될 때 실행 (시스템 설정상의 문제를 확인하는데 도움을 줄 수 있다)
    @EventListener
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        //log.debug("Event! : hi hello!");
        //do more what you want..
    }

}
