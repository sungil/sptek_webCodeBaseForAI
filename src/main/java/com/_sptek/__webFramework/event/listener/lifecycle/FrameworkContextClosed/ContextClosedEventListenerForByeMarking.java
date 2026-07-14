package com._sptek.__webFramework.event.listener.lifecycle.FrameworkContextClosed;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Spring context 종료 이벤트를 수신해 프레임워크 종료 로그를 남기는 listener.
 *
 * <p>실제 종료 제어를 담당하지 않고, Logback 프레임워크 시작/종료 로그 기준에 맞춘 마킹 로그만 처리한다.</p>
 */
@Slf4j
@Component
public class ContextClosedEventListenerForByeMarking {

    /**
     * context close 시점에 종료 완료 메시지를 프레임워크 로그 형식으로 기록한다.
     */
    @EventListener
    public void listen(ContextClosedEvent contextClosedEvent) {
        log.info(LoggingUtil.makeBaseForm(LoggingConstants.FW_START_LOG_TAG, "Context Closed Event", "Bye~ Bye~ Application has been shut down successfully."));
        //do more what you want..
    }
}
