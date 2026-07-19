package com._sptek.__webFramework.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Spring Security 인증 관리자와 method security 공통 설정을 제공한다.
 *
 * <p>개별 SecurityFilterChain은 {@link FrameworkSecurityFilterChainConfig}와 프로젝트 공통 설정에서 구성하고,
 * 이 클래스는 Spring Security에 등록된 {@link AuthenticationProvider}들을 사용하는
 * AuthenticationManager 생성 책임만 가진다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityEnableConfig {

    /**
     * HttpSecurity가 공유하는 AuthenticationManagerBuilder를 기반으로 AuthenticationManager를 생성한다.
     *
     * <p>AuthenticationProvider 구현체는 Bean으로 등록되면 Spring Security 구성 과정에서
     * AuthenticationManagerBuilder에 반영된다. 여기서는 provider를 수동으로 다시 추가하지 않고,
     * credentials 제거 정책만 명시한 뒤 공통 AuthenticationManager를 build한다.</p>
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.eraseCredentials(true);
        return authenticationManagerBuilder.build();
    }
}
