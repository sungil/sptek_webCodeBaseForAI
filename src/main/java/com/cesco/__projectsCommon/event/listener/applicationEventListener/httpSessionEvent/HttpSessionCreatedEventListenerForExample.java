package com.cesco.__projectsCommon.event.listener.applicationEventListener.httpSessionEvent;

import com._sptek.__webFramework.event.httpSession.HttpSessionListenerAdapter;
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
