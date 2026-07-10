package com._sptek.__webFramework.web.binding;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfig implements WebMvcConfigurer {
    private final ApplicationContext applicationContext;

    // controller에서 request 데이터를 object로 바인딩 해줄때 단순 바인딩이 아니라 HandlerMethodArgumentResolver를 구현한 것들이 있으면 그에 따라 처리해 줌.
    // 일일이 HandlerMethodArgumentResolver를 등록하던 방식에서 더 나은 방식으로 변경함.
    //@Override
    //public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    //    //구현한 ArgumentResolver를 등록해 준다.
    //    resolvers.add(new ArgumentResolverForMyUserDto());
    //    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    //}

    @Override
    // Spring 컨텍스트에서 모든 HandlerMethodArgumentResolver 빈을 검색 하여 자동 등록 하는 방식 으로 변경함
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> HandlerMethodArgumentResolvers) {
        HandlerMethodArgumentResolvers.addAll(applicationContext.getBeansOfType(HandlerMethodArgumentResolver.class).values());
    }
}
