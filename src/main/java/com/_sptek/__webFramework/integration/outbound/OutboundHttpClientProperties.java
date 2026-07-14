package com._sptek.__webFramework.integration.outbound;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 외부 HTTP 호출에 사용할 Apache HttpClient pool/timeout 값을 yml에서 주입받는 설정.
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.outbound.http-client")
public class OutboundHttpClientProperties {
    private Pool pool = new Pool();
    private Timeout timeout = new Timeout();

    @Data
    public static class Pool {
        private int maxConnTotal = 100;
        private int maxConnPerRoute = 20;
        private int keepAliveSeconds = 10;
    }

    @Data
    public static class Timeout {
        private int connectionRequestSeconds = 5;
        private int connectSeconds = 5;
        private int responseSeconds = 10;
    }
}
