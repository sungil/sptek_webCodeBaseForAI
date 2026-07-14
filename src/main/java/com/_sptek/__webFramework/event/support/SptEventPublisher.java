package com._sptek.__webFramework.event.support;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 프레임워크와 프로젝트 공통 코드에서 커스텀 Spring 이벤트를 발행할 때 사용하는 래퍼.
 *
 * <p>이벤트 타입을 {@link SptBaseEvent} 계열로 제한해 Base 코드의 이벤트 모델을 일관되게 유지한다.
 * 실제 배포는 Spring {@link ApplicationEventPublisher}에 위임한다.</p>
 */
@Component
@RequiredArgsConstructor
public class SptEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * {@link SptBaseEvent}를 상속한 커스텀 이벤트를 Spring application event로 발행한다.
     */
    public void publishEvent(SptBaseEvent SptBaseEvent) {
        applicationEventPublisher.publishEvent(SptBaseEvent);
    }
}
