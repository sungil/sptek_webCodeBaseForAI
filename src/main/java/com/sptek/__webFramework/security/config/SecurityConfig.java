package com.sptek.__webFramework.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Spring Security 인증 관리자와 method security 공통 설정을 제공한다.
 *
 * <p>개별 SecurityFilterChain은 {@link FrameworkSecurityFilterChainConfig}와 프로젝트 공통 설정에서 구성하고,
 * 이 클래스는 등록된 AuthenticationProvider들을 사용하는 AuthenticationManager 생성 책임만 가진다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) //컨트롤로에서 개별적 권한 관리가 가능
public class SecurityConfig {

    /**
     * HttpSecurity가 공유하는 AuthenticationManagerBuilder를 기반으로 AuthenticationManager를 생성한다.
     */
    @Bean
    //로그인 전체 스텝을 관리할 AuthenticationManager(=ProviderManager)에 AuthenticationProvider을 추가하여 반환. (필요에 따라 만들어진 AuthenticationProvider)
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        //AuthenticationProvider 가 여러개 설정된 상황에 대해서 어떤 전략?으로 처리할지 커스텀이 필요하다면
        //AuthenticationManager 에 대한 custom 작업이 필요함!

        AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        //customAuthenticationProvider가 @Component로 구성되서 그런지.. 자동으로 감지해서 설정이 되는듯 함(이코드를 넣으면 프로바이드가 두번 설정됨)
        //authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
        authenticationManagerBuilder.eraseCredentials(true);
        return authenticationManagerBuilder.build();
    }

//    //@HasAnnotationOnMain(UniversalAnnotationForTest.class)
//    @Bean
//    // 주로 SecurityFilterChain 에서 특정 경로(js, css resource 경로등)를 제외하는 용도로 사용
//    // 아래 securityFilterChain에서 도 유사하게 처리 할수 있으나.. 이곳에 설정한 경로는 spring-security 와 관련한 모든 설정이 적용되지 않음 (다른 필터에서 security 관련 사용시 주위 필요)
//    // spring 로그에서 권장되지 않는 방식이라 WARN이 발생함, 추후 필터 체인에서 나머지 경로에 대한 permitAll() 설정으로 대체하는 것이 좋음
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (webSecurity) -> webSecurity.ignoring()
//                .requestMatchers(SecurityUtil.getNotEssentialRequestPatternsArray())
//                ;
//        //return (webSecurity) -> webSecurity.ignoring().requestMatchers("/**");
//    }
}
