package com._sptek._webFrameworkExample.aiExample.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiExampleEventListener {

    @EventListener
    public void listen(AiExampleEvent event) {
        log.debug("[aiExample] event received: {}", event);
    }
}
