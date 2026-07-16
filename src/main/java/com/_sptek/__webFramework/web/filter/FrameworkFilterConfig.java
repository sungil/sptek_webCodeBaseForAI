package com._sptek.__webFramework.web.filter;

import com._sptek.__webFramework.observability.logging.ReqResDetailLogFilter;
import com._sptek.__webFramework.observability.logging.ReqResDetailLogProperties;
import com._sptek.__webFramework.observability.mdc.MakeMdcFilter;
import com._sptek.__webFramework.observability.timing.MakeRequestTimestampFilter;
import com._sptek.__webFramework.web.cors.CorsPolicyFilter;
import com._sptek.__webFramework.web.cors.Enable_CorsPolicyFilter_At_Main;
import com._sptek.__webFramework.observability.mdc.Enable_MdcTagging_At_Main;
import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com._sptek.__webFramework.web.cors.CorsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.filter.RequestContextFilter;

/**
 * 프레임워크 공통 Servlet Filter 등록 순서와 활성화 조건을 관리하는 설정.
 *
 * <p>메인 클래스의 {@code Enable_*_At_Main} 애노테이션과 profile 조건에 따라 MDC, minor request 세션 제외,
 * 요청 시각 기록, request/response 상세 로그, CORS 정책 필터를 등록한다.</p>
 */
@Slf4j
@Configuration
public class FrameworkFilterConfig {
    // Spring Security 전후로 필요한 필터 위치를 명시한다. ReqResDetailLogFilter는 HandlerMethod 결정 이후 로그 출력을 전제로 한다.
    private static final int REQUEST_CONTEXT_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE;
    private static final int REQUEST_TIMESTAMP_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 5;
    private static final int NO_SESSION_FILTER_FOR_MINOR_REQUEST_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;
    private static final int MDC_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 20;
    private static final int CORS_POLICY_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 30;
    private static final int REQ_RES_DETAIL_LOG_FILTER_ORDER = 10;

    /**
     * 필터 내부에서도 Spring request context를 조회할 수 있게 RequestContextFilter를 가장 앞쪽에 등록한다.
     */
    @Profile(value = { "local", "dev", "stg", "prd" })
    @Bean
    // todo : 아래 custom 필터 내부에서도 RequestContextHolder 를 통해 정보를 사용할수 있도록 하기 위해 우선 순위를 높여 설정함
    // 가장 좋은 방법은 필터 레이어에서는 RequestContextHolder 를 직접 사용하지 않는 것이 좋음
    public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
        FilterRegistrationBean<RequestContextFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new org.springframework.web.filter.RequestContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(REQUEST_CONTEXT_FILTER_ORDER);  // Spring 필터 순서를 높게 설정
        return filterRegistrationBean;
    }

    /**
     * 요청 시작 시각을 request attribute에 남기는 필터를 등록한다.
     */
    @Profile(value = { "local", "dev", "stg", "prd" })
    @Bean
    public FilterRegistrationBean<MakeRequestTimestampFilter> makeRequestTimestampFilter() {
        FilterRegistrationBean<MakeRequestTimestampFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeRequestTimestampFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(REQUEST_TIMESTAMP_FILTER_ORDER);
        //filterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        return filterRegistrationBean;
    }

    /**
     * static 또는 중요도가 낮은 요청에서 Spring Session 저장소 접근을 줄이는 필터를 등록한다.
     */
    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_At_Bean(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)
    @Bean
    public FilterRegistrationBean<NoSessionFilterForMinorRequest> noSessionFilterForMinorRequest() {
        FilterRegistrationBean<NoSessionFilterForMinorRequest> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new NoSessionFilterForMinorRequest());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(NO_SESSION_FILTER_FOR_MINOR_REQUEST_ORDER);
        return filterRegistrationBean;
    }

    /**
     * 로그 패턴에서 사용할 사용자/세션/correlationId MDC 값을 구성하는 필터를 등록한다.
     */
    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_At_Bean(Enable_MdcTagging_At_Main.class)
    @Bean
    public FilterRegistrationBean<MakeMdcFilter> makeMdcFilter() {
        FilterRegistrationBean<MakeMdcFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeMdcFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(MDC_FILTER_ORDER);
        return filterRegistrationBean;
    }

    /**
     * API 경로에 CORS 응답 헤더와 preflight 응답 처리를 적용하는 필터를 등록한다.
     */
    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_At_Bean(Enable_CorsPolicyFilter_At_Main.class)
    @Bean
    public FilterRegistrationBean<CorsPolicyFilter> corsPolicyFilter(CorsProperties corsProperties) {
        //log.debug("corsPolicyFilter is applied.");
        FilterRegistrationBean<CorsPolicyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new CorsPolicyFilter(corsProperties));
        filterRegistrationBean.addUrlPatterns("/api/*");
        filterRegistrationBean.setOrder(CORS_POLICY_FILTER_ORDER);
        return filterRegistrationBean;
    }

    /**
     * 요청/응답 상세 로그 출력을 위해 body caching wrapper를 적용하는 필터를 등록한다.
     *
     * <p>실제 로그 대상 여부는 HandlerMethod 확인이 가능한 interceptor 단계에서 결정된다.</p>
     */
    @Profile(value = { "local", "dev", "stg", "prd" }) // 필터 내부에서 동작을 여부가 다시 한번 결정됨
    @Bean
    public FilterRegistrationBean<ReqResDetailLogFilter> detailLogFilterWithAnnotation(ReqResDetailLogProperties reqResDetailLogProperties) {
        FilterRegistrationBean<ReqResDetailLogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ReqResDetailLogFilter(reqResDetailLogProperties));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(REQ_RES_DETAIL_LOG_FILTER_ORDER);
        //filterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        return filterRegistrationBean;
    }
}

