package com._sptek.__webFramework.web.publicResourceCache;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.time.Duration;

/**
 * Swagger webjar와 애플리케이션 static resource 경로를 등록하는 MVC 전역 설정.
 *
 * <p>메인 클래스의 {@link Enable_HttpCachePublicForStaticResource_At_Main} 적용 여부에 따라
 * static resource에 장기 public cache와 content hash 기반 URL 버전 전략을 붙일지 결정한다.</p>
 */
@Slf4j
@Configuration
public class ResourceHandlerConfig implements WebMvcConfigurer {
    private static final String[] STATIC_RESOURCE_LOCATIONS = {
            "classpath:/cesco/_sales/static/",
            "classpath:/cesco/_marketing/static/",
            "classpath:/cesco/__projectsCommon/static/",
            "classpath:/_sptek/_webFrameworkExample/static/",
            "classpath:/_sptek/__webFramework/static/"
    };

    /**
     * SpringDoc/Swagger UI가 사용하는 classpath resource 경로를 등록한다.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        // Web static 리소스에 대한 설정은 프로퍼티 spring.web.resources.static-locations: 를 통해서도 설정 가능
        // static-locations: #static resource 에 대한 디폴트 경로 및 표기를 지정함(여러 모양으로 지정가능), 설정이 없다면 resource/static/xxx 를 /xxx로 접근할수 있음
        // - classpath:/resources/ #/resource/static/xxx 를 /xxx로 접근함 (디폴트 설정과 같은 내용)
        // - classpath:/static/ #/resource/static/xxx 를 /static/xxx로 접근함

        //swagger를 위한 리소스핸들러 설정
        resourceHandlerRegistry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/resources/");
        resourceHandlerRegistry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/resources/webjars/");
        resourceHandlerRegistry.addResourceHandler("/js/_framework/**").addResourceLocations("classpath:/_sptek/_webFrameworkExample/_framework/");
    }

    /**
     * static resource에 public cache와 content hash 기반 URL 버전을 적용하는 조건부 설정.
     */
    @HasAnnotationOnMain_At_Bean(Enable_HttpCachePublicForStaticResource_At_Main.class)
    @Configuration
    public class EnableHttpCachePublicForStaticResource implements WebMvcConfigurer {
        /**
         * 실행 프로젝트, 프로젝트 공통, 프레임워크 static resource를 전체 경로에 매핑하고
         * 장기 cache header와 version resolver를 붙인다.
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {

            // 프로퍼티 속성 spring.web.resources.static-locations의 설정의 역할과 동일,
            // 양쪽에 둘다 설정 될수 있음(양쪽 설정 모두 적용됨, 그러나 프로퍼티 속성이 없는 경우는 /static 하위를 /**로 매핑한것으로 디포트 설정됨을 주의)
            // VersionResourceResolver 의 경우 thymeleaf 내에서만 동작함으로
            // 그럼으로 thymeleaf 경로 밖의 예를 들어 /static/js/ js파일 내부에서 다른 js 파일을 import 하는 경우 적용이 안됨(cache busting 에 주의)
            resourceHandlerRegistry.addResourceHandler("/**")
                    .addResourceLocations(STATIC_RESOURCE_LOCATIONS) //todo: 참고-static 파일이 변경된 경우는 서버 재시작을 해야 캐시값이 새로 적용됨
                    .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic())
                    .resourceChain(true)
                    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

            // todo: 아래와 같이 리소스 핸들러 경로에 프리픽스(/static/)를 주면 리소스 인식에는 문제가 없는데..
            //  VersionResourceResolver가 적용되지 않는(리소스에 해싱값이 안붙음) 현상이 있음 (원인 확인 필요), thymeleaf 버그 일수도..
            //  resourceHandlerRegistry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl);
        }

        /**
         * Thymeleaf가 렌더링하는 static resource URL에 version hash를 인코딩하게 하는 필터를 등록한다.
         */
        @Bean
        //static resource 의 버전(해싱값) 처리를 위해 필요
        public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
            return new ResourceUrlEncodingFilter();
        }

    }

    /**
     * static resource public cache 기능을 사용하지 않을 때 새 resource 구조의 static 경로만 등록하는 조건부 설정.
     */
    @HasAnnotationOnMain_At_Bean(value = Enable_HttpCachePublicForStaticResource_At_Main.class, negate = true)
    @Configuration
    public class DisableHttpCachePublicForStaticResource implements WebMvcConfigurer {
        /**
         * cache header나 resource chain 없이 static resource 기본 경로를 등록한다.
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
            resourceHandlerRegistry.addResourceHandler("/**").addResourceLocations(STATIC_RESOURCE_LOCATIONS);
        }
    }
}

