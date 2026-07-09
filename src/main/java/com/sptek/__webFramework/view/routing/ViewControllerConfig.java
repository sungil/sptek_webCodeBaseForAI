package com.sptek.__webFramework.view.routing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 별도 Controller 클래스 없이 처리할 수 있는 단순 view route를 등록하는 MVC 전역 설정.
 *
 * <p>정적 redirect나 예제 index 같은 단순 화면 매핑만 이곳에 두고,
 * 모델 구성, 권한 판단, 예외 처리 연계가 필요한 화면은 명시적인 Controller를 사용한다.</p>
 */
@Slf4j
@Configuration
public class ViewControllerConfig implements WebMvcConfigurer {

    /**
     * Swagger redirect와 기본 예제 화면의 view name 매핑을 등록한다.
     *
     * <p>ViewControllerRegistry 기반 매핑은 일반 Controller 메서드를 거치지 않으므로
     * ControllerAdvice 의존 처리가 필요한 화면에는 사용하지 않는다.</p>
     */
    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
        //for swagger.
        viewControllerRegistry.addRedirectViewController("/api/demo-ui.html", "/demo-ui.html");

        //별도 컨트럴러 매핑 없이 view로 넘어가도록 설정 (이경우 @ControllerAdvice 가 동작 하지 않음을 주의)
        viewControllerRegistry.addViewController("/").setViewName("/pages/_example/unit/index");
        viewControllerRegistry.addViewController("/fileUpload").setViewName("/pages/_example/html/fileUpload");
    }

    /*
    @Override
    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {
        //thymeleaf 설정을 application.yml 에서 설정하고 있어서 사용하지 않도록 처리됨

        //jsp로 설정이 필요한 경우
        viewResolverRegistry.jsp("/WEB-INF/views/", ".jsp");
        WebMvcConfigurer.super.configureViewResolvers(viewResolverRegistry);
    }
    */
}
