package com.sptek._projectsCommon.interceptor;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final ExampleInterceptor exampleInterceptor;


    @Override
    public void addInterceptors(@NotNull InterceptorRegistry interceptorRegistry) {

        ////필요한 interceptor 등록 (exampleInterceptor 참고)
        //if(exampleInterceptor != null) {
        //    interceptorRegistry.addInterceptor(this.exampleInterceptor).addPathPatterns("/*/01/projectName/interceptor/**")
        //            .excludePathPatterns("/api/**")
        //            .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
        //            .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns())
        //    ;
        //}

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}
