package com.sptek.__webFramework.example.unit.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyEventListenerForXX {

    @EventListener
    public void listen(MyEvent myEvent) {
        log.debug("Event! : {}", myEvent.toString());
    }
}
