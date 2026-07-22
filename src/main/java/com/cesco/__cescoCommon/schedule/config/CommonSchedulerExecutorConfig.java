package com.cesco.__cescoCommon.schedule.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor

public class CommonSchedulerExecutorConfig {
    final private Environment environment;

    // Spring 공용 ThreadPoolTaskScheduler 설정
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3); // 여러 @Scheduled 로 인한 지연 처리를 줄이기 위해 default 1에서 증가 시킴
        scheduler.setThreadNamePrefix("from common threadPoolTaskScheduler-");
        scheduler.setRemoveOnCancelPolicy(true); //true = 스케줄 작업이 cancel() 시 대기큐 바로 삭제
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // spring 종료시 진행중 작업 마무리 가능하도록 설정
        scheduler.setAwaitTerminationSeconds((int)environment.getProperty(
                "spring.lifecycle.timeout-per-shutdown-phase", Duration.class, Duration.ofSeconds(30)).getSeconds()); // 마무리 작업 최대 보장 시간
        return scheduler;
    }
}

