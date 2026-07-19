package com._sptek.__webFramework.security.config;

import com._sptek.__webFramework.security.authentication.view.CustomAuthenticationFailureHandlerForView;
import com._sptek.__webFramework.security.authentication.view.CustomAuthenticationSuccessHandlerForView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * 프레임워크 기본 Spring Security filter chain들을 경로와 profile별로 나누어 구성한다.
 *
 * <p>system support, actuator, Swagger/H2 처럼 프레임워크가 직접 제공하는 경로의 인증/인가 정책을 담당한다.
 * 실제 프로젝트 업무 경로와 example 업무 경로 정책은 각 업무 프로젝트의 SecurityFilterChain 설정에서 추가한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class FrameworkSecurityFilterChainConfig {

    private final CustomAuthenticationSuccessHandlerForView customAuthenticationSuccessHandlerForView;
    private final CustomAuthenticationFailureHandlerForView customAuthenticationFailureHandlerForView;

//    // 다른 방식으로 대체 함
//    private final CustomAuthenticationProvider customAuthenticationProvider;

//    @Bean
//    public SecurityFilterChain securityFilterChainForRoot(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .securityMatcher("/")
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/").permitAll()
//                );
//        return httpSecurity.build();
//    }

    /**
     * 운영 profile에서 Swagger, H2 경로를 차단한다.
     */
    @Bean
    @Order(10)
    @Profile(value = {"prd"})
    public SecurityFilterChain securityFilterChainForBlockOnPrd(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/h2-console/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().denyAll()
                );

        return httpSecurity.build();
    }

    /**
     * dev/stg profile에서 H2 경로를 차단한다.
     */
    @Bean
    @Order(10)
    @Profile(value = {"dev", "stg"})
    public SecurityFilterChain securityFilterChainForBlockOnStgDev(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/h2-console/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().denyAll()
                );

        return httpSecurity.build();
    }


    /**
     * 로그인, 로그아웃, index 같은 시스템 View 지원 경로의 form login 정책을 구성한다.
     */
    @Bean
    @Order(11)
    public SecurityFilterChain securityFilterChainForSystemSupportView(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/", "/view/index", "/view/login", "/view/loginProcess", "/view/logout")

                // CSRF를 비활성화할 경로 지정
                .csrf(csrf -> csrf
                        //.ignoringRequestMatchers("/**") // NOTE: 테스트를 편하게 하기 위해 모든 경로에서 dsrf 토큰을 무시할 경우
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )

                // .httpBasic(Customizer.withDefaults()) //얼럿창형
                // .formLogin(withDefaults()) //form형 디폴트 로그인 (--:8443/login 으로 고정되어 있는듯 8443 포트에서만 정상 동작됨)
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .loginProcessingUrl("/view/loginProcess") // 실제 컨트럴 존재하지 않음, 해당 요청시 CustomAuthenticationProvider 에서 처리함
                        //.defaultSuccessUrl("/")
                        .successHandler(customAuthenticationSuccessHandlerForView)
                        .failureHandler(customAuthenticationFailureHandlerForView)
                )
                .logout(logout -> logout
                                // 로그아웃 처리 url 설정 (해당 req 매핑이 존재할 필요는 없음)
                                .logoutUrl("/view/logout")

                        // 추가적인 로직이 필요한 경우
                        //.logoutSuccessHandler((request, response, authentication) -> {
                        //    log.debug("User has logged out: " + (authentication != null ? authentication.getName() : "Anonymous"));
                        //    response.sendRedirect("{logoutUrl}?logout"); // custom 코드를 넣었다면 마지막 리다이렉션 처리까지 직접 해줘야함.
                        //})

                );

        return httpSecurity.build();
    }

    /**
     * 시스템 지원 API와 error 경로를 인증 없이 접근 가능하게 구성한다.
     */
    @Bean
    @Order(20)
    public SecurityFilterChain securityFilterChainForSystemSupportApi(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/systemSupportApi/**", "/error/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/systemSupportApi/**", "/error/**").permitAll() //NOTE: 필요시 경로를 더 구체화해서 적용할 것
                        .anyRequest().authenticated()
                );

        return httpSecurity.build();
    }

    /**
     * Actuator는 health만 공개하고 나머지는 인증이 필요하게 구성한다.
     */
    @Bean
    @Order(30)
    public SecurityFilterChain securityFilterChainForActuator(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/actuator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated() //나머지 정보는 보안상 인증 상태일때만 제공
                );

        return httpSecurity.build();
    }

    /**
     * local/dev/stg profile에서 Swagger/OpenAPI 문서 경로를 공개한다.
     */
    @Bean
    @Order(40)
    @Profile(value = {"local", "dev", "stg"})
    public SecurityFilterChain securityFilterChainForSwaggerSupport(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                );

        return httpSecurity.build();
    }

    /**
     * local profile에서 H2 console 접근과 frame rendering을 허용한다.
     */
    @Bean
    @Order(41)
    @Profile(value = {"local"})
    public SecurityFilterChain securityFilterChainForH2Console(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/h2-console/**")
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        //콘솔 UI 구성상 FrameOptionsConfig::disable 옵션이 필요힘(보안상 필요 경로만 적용)
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                );

        return httpSecurity.build();
    }
}
