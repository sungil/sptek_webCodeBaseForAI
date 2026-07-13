package com._sptek._webFrameworkExample.aiExample.observability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiExampleScheduler {

    public void runSampleTask() {
        log.debug("[aiExample] scheduled task body");
    }
}
