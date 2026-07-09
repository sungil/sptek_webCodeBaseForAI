package com.sptek.__webFramework.legacy.applicationContextInitializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
//@Component
public class ContextInitializerFor implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    //애플리케이션에서 스프링 컨텍스트 초기화 전에 커스텀 설정이나 로직을 실행하기 위해 사용 할수 있다.
    //application 메인 함수에 아래와 같은 방법으로 ApplicationContextInitializer 를 동작 시킬수 있다.
    //public static void main(String[] args) {
    //    new SpringApplicationBuilder(SptWfwApplication.class)
    //            .initializers(new ContextInitializerFor())
    //            .run(args);
    //}

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.debug("ApplicationContext 초기화 중!");
        //System.setProperty("springdoc.api-docs.enabled", "false");
    }
}
