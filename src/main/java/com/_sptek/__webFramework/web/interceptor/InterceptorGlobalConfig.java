package com._sptek.__webFramework.web.interceptor;


import com._sptek.__webFramework.api.deduplicationRequest.PreventDuplicateRequestInterceptor;
import com._sptek.__webFramework.observability.logging.ReqResDetailLogDecisionInterceptor;
import com._sptek.__webFramework.observability.logging.VisitHistoryLoggingInterceptor;
import com._sptek.__webFramework.view.error.ViewErrorLogSupportInterceptor;
import com._sptek.__webFramework.security.util.SecurityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 프레임워크 공통 MVC interceptor의 등록 순서와 적용 경로를 관리하는 전역 설정.
 *
 * <p>각 interceptor는 메인 애노테이션 조건에 따라 Bean 등록이 달라질 수 있으므로 nullable로 주입받는다.
 * 로그 대상 결정처럼 후속 interceptor나 filter가 의존하는 값은 먼저 등록한다.</p>
 */
@Configuration
public class InterceptorGlobalConfig implements WebMvcConfigurer {

    private final PreventDuplicateRequestInterceptor preventDuplicateRequestInterceptor;
    private final ReqResDetailLogDecisionInterceptor reqResDetailLogDecisionInterceptor;
    private final VisitHistoryLoggingInterceptor visitHistoryLoggingInterceptor;
    private final ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor;

    //조건에 따라 Interceptor 들이 Bean 으로 등독 될수도 안 될수도 있는 상황이 있기 때문에 @Nullable 을 사용한 생성자 를 직접 구현 하였음
    public InterceptorGlobalConfig(@Nullable PreventDuplicateRequestInterceptor preventDuplicateRequestInterceptor
            , @Nullable ReqResDetailLogDecisionInterceptor reqResDetailLogDecisionInterceptor
            , @Nullable VisitHistoryLoggingInterceptor visitHistoryLoggingInterceptor
            , @Nullable ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor) {
        this.preventDuplicateRequestInterceptor = preventDuplicateRequestInterceptor;
        this.reqResDetailLogDecisionInterceptor = reqResDetailLogDecisionInterceptor;
        this.visitHistoryLoggingInterceptor = visitHistoryLoggingInterceptor;
        this.viewErrorLogSupportInterceptor = viewErrorLogSupportInterceptor;
    }

    /**
     * 등록된 interceptor Bean만 선택해 API/View 경로별로 적용한다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {

        // 원하는 순서로 interceptor 등록
        // ReqResDetailLogDecisionInterceptor 는 다른 인터셉터/컨트롤러에서 사용할 로그 대상 attribute 를 먼저 세팅한다.
        if(reqResDetailLogDecisionInterceptor != null) {
            interceptorRegistry.addInterceptor(this.reqResDetailLogDecisionInterceptor).addPathPatterns("/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

        if(preventDuplicateRequestInterceptor != null) {
            interceptorRegistry.addInterceptor(this.preventDuplicateRequestInterceptor).addPathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

        //필요한 interceptor 등록 (exampleInterceptor 참고)
        if(visitHistoryLoggingInterceptor != null) {
            interceptorRegistry.addInterceptor(this.visitHistoryLoggingInterceptor).addPathPatterns("/**")
                    .excludePathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

        if(viewErrorLogSupportInterceptor != null) {
            interceptorRegistry.addInterceptor(this.viewErrorLogSupportInterceptor).addPathPatterns("/**")
                    .excludePathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}
