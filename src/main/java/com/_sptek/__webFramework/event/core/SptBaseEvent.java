package com._sptek.__webFramework.event.core;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 프로젝트 커스텀 이벤트가 공통으로 상속하는 기본 이벤트 모델.
 *
 * <p>이벤트 메시지와 함께 생성 시점 기준의 eventId, timestamp를 제공한다.
 * 업무 이벤트는 이 타입을 상속해 도메인별 payload를 추가하고 {@code SptEventPublisher}를 통해 발행한다.</p>
 */
@Getter
@ToString
@SuperBuilder
public class SptBaseEvent {

    private final String eventMessage;

    @Builder.Default //기본값으로 지정됌
    private final long eventId = System.currentTimeMillis();

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
}
