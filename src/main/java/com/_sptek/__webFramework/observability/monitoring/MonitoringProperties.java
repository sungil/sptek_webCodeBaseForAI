package com._sptek.__webFramework.observability.monitoring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 프레임워크 모니터링 scheduler들의 실행 주기와 중복 로그 억제 정책을 yml에서 주입받는 설정.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.monitoring")
public class MonitoringProperties {
    private Scheduler hikariDatasource = new Scheduler();
    private Scheduler httpConnectorWorker = new Scheduler();
    private Scheduler outboundSupport = new Scheduler();

    @Data
    public static class Scheduler {
        private int fixedDelaySeconds = 10;
        private boolean duplicateLogSuppressionMode = true;
    }
}
