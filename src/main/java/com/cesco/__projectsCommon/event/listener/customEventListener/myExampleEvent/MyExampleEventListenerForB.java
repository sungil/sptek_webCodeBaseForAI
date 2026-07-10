package com.cesco.__projectsCommon.event.listener.customEventListener.myExampleEvent;

import com.cesco.__projectsCommon.event.event.MyExampleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyExampleEventListenerForB {

    @EventListener
    public void listen(MyExampleEvent myExampleEvent) {
        log.debug("Event! : {}", myExampleEvent.toString());
    }
}
