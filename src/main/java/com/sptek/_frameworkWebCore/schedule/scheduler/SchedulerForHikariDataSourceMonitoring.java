package com.sptek._frameworkWebCore.schedule.scheduler;

import com.sptek._frameworkWebCore._annotation.Enable_HikariDataSourceMonitoring_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.util.ExceptionUtil;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * 등록된 HikariDataSource들의 pool 상태와 주요 설정값을 주기적으로 로그로 남기는 scheduler.
 *
 * <p>{@link Enable_HikariDataSourceMonitoring_At_Main}이 활성화된 경우에만 등록된다.
 * context refresh 이후 datasource를 한 번 pre-warm 한 뒤 MXBean 값을 조회한다.</p>
 */
@Slf4j
@Component
@HasAnnotationOnMain_At_Bean(Enable_HikariDataSourceMonitoring_At_Main.class)

public class SchedulerForHikariDataSourceMonitoring {

    private final ThreadPoolTaskScheduler schedulerExecutorForHikariDataSourceMonitoring;
    private final boolean isDuplicateLogSuppressionMode; // 동일 내용 로깅 방지
    private final int fixedDelaySeconds;
    private Map<String, HikariDataSource> hikariDataSources = null;
    private ScheduledFuture<?> scheduledFuture = null;
    private String logTag;
    private volatile String lastLogContent = "";

    public SchedulerForHikariDataSourceMonitoring(@Qualifier("schedulerExecutorForHikariDataSourceMonitoring") ThreadPoolTaskScheduler schedulerExecutorForHikariDataSourceMonitoring,
                                                  @Value("${logging.monitoring.schedulerForHikariDataSourceMonitoring.duplicateLogSuppressionMode:false}") boolean isDuplicateLogSuppressionMode,
                                                  @Value("${logging.monitoring.schedulerForHikariDataSourceMonitoring.fixedDelaySeconds:5}") int fixedDelaySeconds) {
        this.schedulerExecutorForHikariDataSourceMonitoring = schedulerExecutorForHikariDataSourceMonitoring;
        this.isDuplicateLogSuppressionMode = isDuplicateLogSuppressionMode;
        this.fixedDelaySeconds = fixedDelaySeconds;
    }

    /**
     * context refresh 이후 datasource 목록과 로그 tag를 준비하고 fixed delay scheduling을 시작한다.
     */
    @EventListener // 시작에 MainClassAnnotationRegister 가 필요 함으로 ContextRefreshedEvent 을 기다려 시작함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;

        hikariDataSources = contextRefreshedEvent.getApplicationContext().getBeansOfType(HikariDataSource.class);
        hikariDataSources.values().forEach(ds -> {
            // 모니터링 전 한번 강제 연결을 통해 활성화 시킴
            try (var conn = ds.getConnection()) {
            } catch (Exception e) {
                log.warn("Failed to pre-warm HikariDataSource: {}", ds, e);
            }
        });

        logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_HikariDataSourceMonitoring_At_Main.class).get("value"), "");
        scheduledFuture = schedulerExecutorForHikariDataSourceMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(fixedDelaySeconds));
    }

    /**
     * context 종료 시 Hikari 모니터링 반복 작업과 전용 scheduler를 정리한다.
     */
    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        schedulerExecutorForHikariDataSourceMonitoring.shutdown();
    }

    /**
     * datasource별 connection pool 상태와 Hikari 설정값을 수집해 모니터링 로그로 출력한다.
     */
    public void doJobs() {

        for (HikariDataSource hikariDataSource : hikariDataSources.values()) {
            HikariConfigMXBean hikariConfigMXBean = hikariDataSource.getHikariConfigMXBean();
            HikariPoolMXBean hikariPoolMXBean = hikariDataSource.getHikariPoolMXBean();

            String logContent = """
                   %s => DB연결(TotalConnections)=%s, 사용중(ActiveConnections)=%s, 사용가능(IdleConnections)=%s, 할당대기(ThreadsAwaitingConnection)=%s
                   [CONFIG] 최대허용(MaximumPoolSize)=%s, 상시대기(MinimumIdle)=%s, ThreadsAwaitingConnection 에서 최대 대기시간(ConnectionTimeout)=%s, 유휴 커넥션 회수 시간(IdleTimeout)=%s DB와 커넥션을 새로 연결하는 시간, DB쪽 타임아웃 보다 작게, refresh 의미 (MaxLifetime)=%s, DB 커넥션 헬스체크 타임아웃, 시간내 응답 없으면 새로 연결(ValidationTimeout)=%s
                   """
                    .formatted(
                            hikariDataSource.getPoolName()
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getTotalConnections, -1)
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getActiveConnections, -1)
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getIdleConnections, -1)
                            , ExceptionUtil.exSafe(hikariPoolMXBean::getThreadsAwaitingConnection, -1)

                            , ExceptionUtil.exSafe(hikariConfigMXBean::getMaximumPoolSize, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getMinimumIdle, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getConnectionTimeout, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getIdleTimeout, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getMaxLifetime, -1)
                            , ExceptionUtil.exSafe(hikariConfigMXBean::getValidationTimeout, -1));

            if (isDuplicateLogSuppressionMode && Objects.equals(logContent, lastLogContent)) return;
            log.info(LoggingUtil.makeBaseForm(logTag, "HikariDataSource Monitoring (Scheduler)", logContent));
            lastLogContent = logContent;
        }
    }
}
