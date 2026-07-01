package com.sptek._frameworkWebCore.interceptor.config;


import com.sptek._frameworkWebCore.interceptor.PreventDuplicateRequestInterceptor;
import com.sptek._frameworkWebCore.interceptor.ReqResDetailLogDecisionInterceptor;
import com.sptek._frameworkWebCore.interceptor.ViewXssProtectInterceptor;
import com.sptek._frameworkWebCore.interceptor.VisitHistoryLoggingInterceptor;
import com.sptek._frameworkWebCore.interceptor.ViewErrorLogSupportInterceptor;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorGlobalConfig implements WebMvcConfigurer {

    private final PreventDuplicateRequestInterceptor preventDuplicateRequestInterceptor;
    private final ReqResDetailLogDecisionInterceptor reqResDetailLogDecisionInterceptor;
    private final VisitHistoryLoggingInterceptor visitHistoryLoggingInterceptor;
    private final ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor;
    private final ViewXssProtectInterceptor viewXssProtectInterceptor;

    //조건에 따라 Interceptor 들이 Bean 으로 등독 될수도 안 될수도 있는 상황이 있기 때문에 @Nullable 을 사용한 생성자 를 직접 구현 하였음
    public InterceptorGlobalConfig(@Nullable PreventDuplicateRequestInterceptor preventDuplicateRequestInterceptor
            , @Nullable ReqResDetailLogDecisionInterceptor reqResDetailLogDecisionInterceptor
            , @Nullable VisitHistoryLoggingInterceptor visitHistoryLoggingInterceptor
            , @Nullable ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor
            , @Nullable ViewXssProtectInterceptor viewXssProtectInterceptor) {
        this.preventDuplicateRequestInterceptor = preventDuplicateRequestInterceptor;
        this.reqResDetailLogDecisionInterceptor = reqResDetailLogDecisionInterceptor;
        this.visitHistoryLoggingInterceptor = visitHistoryLoggingInterceptor;
        this.viewErrorLogSupportInterceptor = viewErrorLogSupportInterceptor;
        this.viewXssProtectInterceptor = viewXssProtectInterceptor;
    }

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

        if(viewXssProtectInterceptor != null) {
            interceptorRegistry.addInterceptor(this.viewXssProtectInterceptor).addPathPatterns("/**")
                    .excludePathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

//        interceptorRegistry.addInterceptor(new InterceptorConfigSupportForRequestMethod(new ExampleInterceptor())
//                //2차 필터 조건, 아래 GET의 경우 1차 대상에 포함되나 무조건 제외, api/v1 POST는 인정, api/v2 POST는 제외
//                .excludePathPattern("/api/**", HttpMethod.GET)
//                .excludePathPattern("/api/v2/**", HttpMethod.POST)
//                ).addPathPatterns("/api/**").excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns()); //1차 필터 조건

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}
