package com._sptek._webFrameworkExample.aiExample.observability;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiExampleSmartLifecycle implements SmartLifecycle {
    private volatile boolean running;

    @Override
    public void start() {
        running = true;
        log.debug("[aiExample] lifecycle started");
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void stop() {
        running = false;
        log.debug("[aiExample] lifecycle stopped");
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
