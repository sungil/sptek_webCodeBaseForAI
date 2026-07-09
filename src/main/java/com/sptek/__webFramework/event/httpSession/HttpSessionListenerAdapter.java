package com.sptek.__webFramework.event.httpSession;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Servlet {@link HttpSessionListener} 이벤트를 Spring application event 형태로 변환하는 어댑터.
 *
 * <p>세션 생성/소멸 콜백을 직접 처리하지 않고 별도 이벤트 객체로 발행해,
 * 프레임워크와 프로젝트 공통 리스너가 {@code @EventListener} 방식으로 동일하게 확장할 수 있게 한다.</p>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class HttpSessionListenerAdapter {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Servlet container의 세션 생명주기 콜백을 Spring 이벤트로 재발행하는 listener Bean을 등록한다.
     */
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent httpSessionEvent) {
                applicationEventPublisher.publishEvent(new HttpSessionCreatedEventAdapter(httpSessionEvent));
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
                applicationEventPublisher.publishEvent(new HttpSessionDestroyedEventAdapter(httpSessionEvent));
            }
        };
    }


    /**
     * 세션 생성 콜백을 Spring 이벤트로 전달하기 위한 adapter event.
     */
    @RequiredArgsConstructor
    public class HttpSessionCreatedEventAdapter {
        public final HttpSessionEvent httpSessionEvent;
    }

    /**
     * 세션 소멸 콜백을 Spring 이벤트로 전달하기 위한 adapter event.
     */
    @RequiredArgsConstructor
    public class HttpSessionDestroyedEventAdapter {
        public final HttpSessionEvent httpSessionEvent;
    }
}
