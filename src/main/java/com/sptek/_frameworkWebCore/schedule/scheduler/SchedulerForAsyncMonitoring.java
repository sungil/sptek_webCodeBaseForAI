package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 프레임워크 Async executor 상태를 주기적으로 로그로 남기는 모니터링 scheduler.
 *
 * <p>{@link Enable_AsyncMonitoring_At_Main}이 활성화된 경우에만 등록되며,
 * 실제 async 처리 기능 자체를 켜거나 끄지는 않는다.</p>
 */
@Slf4j
@Component
@HasAnnotationOnMain_At_Bean(Enable_AsyncMonitoring_At_Main.class)

public class SchedulerForAsyncMonitoring {
    // Async Pool 을 모니터링 할뿐 @Enable_AsyncMonitoring_At_Main 가 적용되지 않아도 Async Pool 은 동작함
    
    private final ThreadPoolTaskScheduler schedulerExecutorForAsyncMonitoring;
    private final TaskExecutor  threadPoolForAsync;
    private final boolean isDuplicateLogSuppressionMode; // 동일 내용 로깅 방지
    private final int fixedDelaySeconds;
    private ScheduledFuture<?> scheduledFuture = null;
    private String logTag;
    private volatile String lastLogContent = "";


    public SchedulerForAsyncMonitoring(
            @Qualifier("schedulerExecutorForAsyncMonitoring") ThreadPoolTaskScheduler schedulerExecutorForAsyncMonitoring,
            @Qualifier("baseTaskExecutor") TaskExecutor threadPoolForAsync,
            @Value("${logging.monitoring.schedulerForAsyncMonitoring.duplicateLogSuppressionMode:false}") boolean isDuplicateLogSuppressionMode,
            @Value("${logging.monitoring.schedulerForAsyncMonitoring.fixedDelaySeconds:5}") int fixedDelaySeconds) {
        this.schedulerExecutorForAsyncMonitoring = schedulerExecutorForAsyncMonitoring;
        this.threadPoolForAsync = threadPoolForAsync;
        this.isDuplicateLogSuppressionMode = isDuplicateLogSuppressionMode;
        this.fixedDelaySeconds = fixedDelaySeconds;
    }

    /**
     * context refresh 이후 애노테이션 속성의 로그 tag를 읽고 fixed delay scheduling을 시작한다.
     */
    @EventListener // @PostConstruct 시점에는 MainClassAnnotationRegister 가 생성되기 전임으로  Event Listen 방식으로 변경함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;
        logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_AsyncMonitoring_At_Main.class).get("value"), "");
        scheduledFuture = schedulerExecutorForAsyncMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(fixedDelaySeconds));
    }

    /**
     * context 종료 시 진행 중 작업은 기다리되, 이후 반복 scheduling은 중단한다.
     */
    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        schedulerExecutorForAsyncMonitoring.shutdown();
    }

    /**
     * Async executor의 max/core/active/queue 상태를 수집해 모니터링 로그로 출력한다.
     */
    public void doJobs() {
        try {
            String logContent;
            if (threadPoolForAsync instanceof ThreadPoolTaskExecutor threadPoolTaskExecutor) {
                ThreadPoolExecutor executor = threadPoolTaskExecutor.getThreadPoolExecutor();
                logContent = String.format("최대허용(maxPoolSize)=%d, 상시대기(corePoolSize)=%d, 사용중(activeCount)=%d, 할당대기(queueSize)=%d",
                        executor.getMaximumPoolSize(),
                        executor.getCorePoolSize(),
                        executor.getActiveCount(),
                        executor.getQueue().size()
                );
            } else {
                logContent = "Not a ThreadPoolTaskExecutor instance: " + threadPoolForAsync.getClass().getName();
            }

            if (isDuplicateLogSuppressionMode && Objects.equals(logContent, lastLogContent)) return;
            log.info(LoggingUtil.makeBaseForm(logTag, "SPT Async Monitoring (Scheduler)", logContent));
            lastLogContent = logContent;

        } catch (Exception e) {
            log.warn("Scheduler For SPT Async Monitoring", e);
        }
    }
}
