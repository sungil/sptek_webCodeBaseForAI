package com.sptek._frameworkWebCore.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
/**
 * util 패키지의 정적 request 접근 기능에 필요한 보조 Spring Bean 설정.
 */
public class UtilBeans {

    /**
     * 현재 요청을 RequestContextHolder에 바인딩해 SpringUtil에서 request/response를 조회할 수 있게 한다.
     */
    @Bean
    public RequestContextListener requestContextListener() {
        // 중요!: 현재 요청(HttpServletRequest)을 RequestContextHolder 에 바인딩 하는 역할을 함, config 성격 으로 추후 변경할 내용 없음
        // 현재 스레드 에서 요청 정보를 전역적 으로 사용 가능, SpringUtil 클레스 를 사용 하기 위해 반드시 필요함
        return new RequestContextListener();
    }
}
