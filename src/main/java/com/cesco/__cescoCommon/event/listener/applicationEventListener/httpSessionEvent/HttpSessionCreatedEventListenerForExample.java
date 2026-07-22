package com.cesco.__cescoCommon.event.listener.applicationEventListener.httpSessionEvent;

import com._sptek.__webFramework.event.listener.httpSession.HttpSessionListenerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpSessionCreatedEventListenerForExample {

    @EventListener
    public void listen(HttpSessionListenerAdapter.HttpSessionCreatedEventAdapter HttpSessionCreatedEventAdapter) {
        log.debug("Event! : created session({})", HttpSessionCreatedEventAdapter.httpSessionEvent.getSession().getId());
        //do more what you want..
    }
}
