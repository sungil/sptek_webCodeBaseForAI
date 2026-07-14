package com._sptek.__webFramework.observability.monitoring;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * embedded Tomcat connector worker pool 상태를 주기적으로 로그로 남기는 scheduler.
 *
 * <p>{@link Enable_HttpConnectorWorkerMonitoring_At_Main}이 활성화된 경우에만 등록된다.
 * TomcatWebServer에서 connector와 executor 정보를 직접 읽으므로 embedded Tomcat 환경에 의존한다.</p>
 */
@Slf4j
@Component
@HasAnnotationOnMain_At_Bean(Enable_HttpConnectorWorkerMonitoring_At_Main.class)

public class SchedulerForHttpConnectorWorkerMonitoring {
    // todo: 현재의 SchedulerForHttpConnectorWorkerMonitoring 는 embeeded tomcat 을 사용하는 경우만 동작함

    private final ThreadPoolTaskScheduler schedulerExecutorForHttpConnectorWorkerMonitoring;
    private final MonitoringProperties monitoringProperties;
    private TomcatWebServer  tomcatWebServer = null;
    private ScheduledFuture<?> scheduledFuture = null;
    private String logTag;
    private final Map<String, String> lastLogContents = new ConcurrentHashMap<>();

    public SchedulerForHttpConnectorWorkerMonitoring(@Qualifier("schedulerExecutorForHttpConnectorWorkerMonitoring") ThreadPoolTaskScheduler schedulerExecutorForHttpConnectorWorkerMonitoring,
                                                     MonitoringProperties monitoringProperties) {
        this.schedulerExecutorForHttpConnectorWorkerMonitoring = schedulerExecutorForHttpConnectorWorkerMonitoring;
        this.monitoringProperties = monitoringProperties;
    }

    /**
     * Servlet web server 초기화 이벤트에서 embedded TomcatWebServer 참조를 확보한다.
     */
    @EventListener // TomcatWebServer 를 얻기 위해 ServletWebServerInitializedEvent 를 listen 하여 가져옴
    public void listen(ServletWebServerInitializedEvent servletWebServerInitializedEvent) {
        if (servletWebServerInitializedEvent.getWebServer() instanceof TomcatWebServer tws) {
            this.tomcatWebServer = tws;
        }
    }

    /**
     * context refresh 이후 애노테이션 속성의 로그 tag를 읽고 fixed delay scheduling을 시작한다.
     */
    @EventListener // 시작에 MainClassAnnotationRegister 가 필요 함으로 ContextRefreshedEvent 을 기다려 시작함
    public void listen(ContextRefreshedEvent contextRefreshedEvent) {
        if (scheduledFuture != null) return;
        logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_HttpConnectorWorkerMonitoring_At_Main.class).get("value"), "");
        scheduledFuture = schedulerExecutorForHttpConnectorWorkerMonitoring.scheduleWithFixedDelay(this::doJobs, Duration.ofSeconds(monitoringProperties.getHttpConnectorWorker().getFixedDelaySeconds()));
    }

    /**
     * context 종료 시 HTTP connector 모니터링 반복 작업과 전용 scheduler를 정리한다.
     */
    @PreDestroy
    public void preDestroy() {
        if (scheduledFuture == null) return;
        scheduledFuture.cancel(false); // 현재 작업이 끝나길 기다리고 중단
        schedulerExecutorForHttpConnectorWorkerMonitoring.shutdown();
    }

    /**
     * Tomcat connector별 max/current/busy/queue 상태를 수집해 모니터링 로그로 출력한다.
     */
    public void doJobs() {
        try {
            for (Connector connector : tomcatWebServer.getTomcat().getService().findConnectors()) {
                ProtocolHandler protocolHandler = connector.getProtocolHandler();
                if (protocolHandler instanceof AbstractProtocol<?> protocol) {
                    int maxThreads = protocol.getMaxThreads();
                    int currentThreads = -1;
                    int busyThreads = -1;
                    int queueSize = -1;

                    // Executor가 실제로 Tomcat ThreadPoolExecutor 타입일 때만 상태 확인
                    var executor = protocol.getExecutor();
                    if (executor instanceof org.apache.tomcat.util.threads.ThreadPoolExecutor threadPoolExecutor) {
                        currentThreads = threadPoolExecutor.getPoolSize();
                        busyThreads = threadPoolExecutor.getActiveCount();
                        queueSize = threadPoolExecutor.getQueue().size();
                    }

                    String logContent = String.format("%s:%d => 최대허용(maxThreads)=%d, 상시대기(currentThreads)=%d, 사용중(busyThreads)=%d, 할당대기(queueSize)=%d",
                            connector.getProtocol(),
                            connector.getPort(),
                            maxThreads,
                            currentThreads,
                            busyThreads,
                            queueSize
                    );

                    String key = connector.getProtocol() + ":" + connector.getPort();
                    if (isDuplicateLog(monitoringProperties.getHttpConnectorWorker(), key, logContent)) continue;
                    log.info(LoggingUtil.makeBaseForm(logTag, "Http Connector Worker Monitoring (Scheduler)", logContent));
                }
            }
        } catch (Exception e) {
            log.warn("Scheduler For Http Connector Worker Monitoring", e);
        }
    }

    private boolean isDuplicateLog(MonitoringProperties.Scheduler schedulerProperties, String key, String logContent) {
        if (!schedulerProperties.isDuplicateLogSuppressionMode()) {
            return false;
        }
        String previousLogContent = lastLogContents.put(key, logContent);
        return Objects.equals(previousLogContent, logContent);
    }
}
