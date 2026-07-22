package com.cesco.__cescoCommon.event.listener.applicationEventListener.httpSessionEvent;

import com._sptek.__webFramework.event.listener.httpSession.HttpSessionListenerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Configuration
public class HttpSessionDestroyedEventListenerForExample {

    @EventListener
    public void listen(HttpSessionListenerAdapter.HttpSessionDestroyedEventAdapter httpSessionDestroyedEventAdapter) {
        log.debug("Event! : destroyed session({})", httpSessionDestroyedEventAdapter.httpSessionEvent.getSession().getId());
        //do more what you want..
    }
}
