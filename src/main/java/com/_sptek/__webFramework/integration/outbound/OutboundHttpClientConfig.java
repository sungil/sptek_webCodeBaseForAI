package com._sptek.__webFramework.integration.outbound;

import com._sptek.__webFramework.bootstrap.registry.RequestMappingAnnotationRegister;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 외부 HTTP 호출에 사용할 Apache HttpClient 기반 Bean 구성을 제공한다.
 *
 * <p>커넥션 풀, timeout, keep-alive 전략을 공통으로 구성하고,
 * 프레임워크의 외부 HTTP 호출은 {@link OutboundSupport}를 공식 진입점으로 사용한다.</p>
 */
@Configuration
public class OutboundHttpClientConfig {
    /**
     * 외부 HTTP 호출 커넥션 풀의 전체/route별 최대 커넥션 수를 설정한다.
     */
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(OutboundHttpClientProperties outboundHttpClientProperties) {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(outboundHttpClientProperties.getPool().getMaxConnTotal());
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(outboundHttpClientProperties.getPool().getMaxConnPerRoute());
        return poolingHttpClientConnectionManager;
    }

    /**
     * 커넥션 풀 대기, TCP 연결, 응답 대기 timeout 기본값을 설정한다.
     */
    @Bean
    public RequestConfig requestConfig(OutboundHttpClientProperties outboundHttpClientProperties) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(outboundHttpClientProperties.getTimeout().getConnectionRequestSeconds(), TimeUnit.SECONDS))
                .setConnectTimeout(Timeout.of(outboundHttpClientProperties.getTimeout().getConnectSeconds(), TimeUnit.SECONDS))
                .setResponseTimeout(Timeout.of(outboundHttpClientProperties.getTimeout().getResponseSeconds(), TimeUnit.SECONDS))
                .build();
    }

    /**
     * 커넥션 풀과 timeout 설정을 적용한 CloseableHttpClient를 등록한다.
     */
    @Bean
    public CloseableHttpClient closeableHttpClient(
            PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
            RequestConfig requestConfig,
            OutboundHttpClientProperties outboundHttpClientProperties) {
        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy((response, context) -> TimeValue.ofSeconds(outboundHttpClientProperties.getPool().getKeepAliveSeconds()))
                .build();
    }

    /**
     * 프레임워크 outbound 호출 래퍼를 등록한다.
     */
    @Bean
    public OutboundSupport outboundSupport(CloseableHttpClient closeableHttpClient, RequestMappingAnnotationRegister requestMappingAnnotationRegister) {
        return new OutboundSupport(closeableHttpClient, requestMappingAnnotationRegister);
    }
}
