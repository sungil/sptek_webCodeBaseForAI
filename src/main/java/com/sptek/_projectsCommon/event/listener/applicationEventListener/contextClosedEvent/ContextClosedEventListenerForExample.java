package com.sptek._projectsCommon.event.listener.applicationEventListener.contextClosedEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextClosedEventListenerForExample {

    @EventListener
    public void listen(ContextClosedEvent contextClosedEvent) {
        //log.debug("Event! : bye bye");
        //do more what you want..
    }
}
