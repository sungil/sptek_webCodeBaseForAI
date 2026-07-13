package com._sptek._webFrameworkExample.aiExample.web.config;

import com._sptek._webFrameworkExample.aiExample.web.argument.AiExampleUserArgumentResolver;
import com._sptek._webFrameworkExample.aiExample.web.filter.AiExampleRequestFilter;
import com._sptek._webFrameworkExample.aiExample.web.interceptor.AiExampleRequestLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AiExampleWebConfig implements WebMvcConfigurer {
    private final AiExampleUserArgumentResolver aiExampleUserArgumentResolver;
    private final AiExampleRequestLogInterceptor aiExampleRequestLogInterceptor;

    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(aiExampleUserArgumentResolver);
    }

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(aiExampleRequestLogInterceptor)
                .addPathPatterns("/api/ai-example/**");
    }

    @Bean
    public FilterRegistrationBean<AiExampleRequestFilter> aiExampleRequestFilterRegistration() {
        FilterRegistrationBean<AiExampleRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AiExampleRequestFilter());
        registrationBean.addUrlPatterns("/api/ai-example/*");
        registrationBean.setName("aiExampleRequestFilter");
        registrationBean.setOrder(Integer.MAX_VALUE);
        return registrationBean;
    }
}
