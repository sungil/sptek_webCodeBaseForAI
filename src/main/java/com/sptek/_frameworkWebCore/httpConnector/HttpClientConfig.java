package com.sptek._frameworkWebCore.httpConnector;

import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.support.OutboundSupport;
import com.sptek._frameworkWebCore.support.DEPRECATED_RestTemplateSupport;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        int HTTP_CLIENT_MAX_CONN_TOTAL = 100; // pool 의 전체 커넥션 갯수
        int HTTP_CLIENT_MAX_CONN_PER_ROUTE = 20; // 한 url 당 커넥션 최대 보유 가능 갯수

        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(HTTP_CLIENT_MAX_CONN_TOTAL);
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(HTTP_CLIENT_MAX_CONN_PER_ROUTE);
        return poolingHttpClientConnectionManager;
    }

    @Bean
    public RequestConfig requestConfig(){
        int DEFAULT_POOL_REQUEST_TIMEOUT = 5; //connection pool 에서 커넥션을 얻어올때까지의 최대 시간 (시간내 못받으면 에러)
        int DEFAULT_CONNECT_TIMEOUT = 5; //서버로 TCP 연결이 완료될 때까지 기다리는 시간 (풀에서 받은 이후 연결 대기 시간)
        int DEFAULT_RESPONSE_TIMEOUT = 10; //연결후 최종 응답 대기 시간

        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.of(DEFAULT_POOL_REQUEST_TIMEOUT,TimeUnit.SECONDS))
                .setConnectTimeout(Timeout.of(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS))
                .setResponseTimeout(Timeout.of(DEFAULT_RESPONSE_TIMEOUT,TimeUnit.SECONDS))
                .build();
    }

    @Bean
    //HttpClient를 대신할 CloseableHttpClient
    public CloseableHttpClient closeableHttpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, RequestConfig requestConfig) {
        int DEFAULT_POOL_KEEP_ALIVE_TIMEOUT = 10; // pool 내 커넥션들이 해당 서버와 커넥션을 유지하고 있는 시간

        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setKeepAliveStrategy((response, context) -> TimeValue.ofSeconds(DEFAULT_POOL_KEEP_ALIVE_TIMEOUT))
                .build();
    }

    @Bean
    //CloseableHttpClient를 쉽게 쓸수있도록 기능 랩핑한 Bean
    public OutboundSupport outboundSupport(CloseableHttpClient closeableHttpClient, RequestMappingAnnotationRegister requestMappingAnnotationRegister){
        return new OutboundSupport(closeableHttpClient, requestMappingAnnotationRegister);
    }

    @Bean
    //reqConfig와 pool 관리를 내부적으로 하고 있는 RestTemplate을 @Autowired 해 사용할 수 있도록 Bean 구성함 (OutboundSupport을 더 권장?)
    public RestTemplate restTemplate(CloseableHttpClient closeableHttpClient){
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpComponentsClientHttpRequestFactory.setHttpClient(closeableHttpClient);
        return new RestTemplate(httpComponentsClientHttpRequestFactory);
    }

    @Bean
    //restTemplate을 쉽게 쓸수있도록 기능 랩핑한 Bean
    public DEPRECATED_RestTemplateSupport restTemplateSupport(RestTemplate restTemplate){
        return new DEPRECATED_RestTemplateSupport(restTemplate);
    }
}
