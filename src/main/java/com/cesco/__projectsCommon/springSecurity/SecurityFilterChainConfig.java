package com.cesco.__projectsCommon.springSecurity;

import com._sptek.__webFramework.security.authentication.view.*;
import com._sptek.__webFramework.security.token.jwt.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityFilterChainConfig {
    private static final String AUTH_SPECIAL_FOR_TEST = "AUTH_SPECIAL_FOR_TEST";

    private final ViewFormLoginSuccessHandler customAuthenticationSuccessHandlerForView;
    private final ViewFormLoginFailureHandler customAuthenticationFailureHandlerForView;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtApiAuthenticationEntryPoint jwtApiAuthenticationEntryPoint;
    private final JwtApiAccessDeniedHandler jwtApiAccessDeniedHandler;

    @Bean
    @Order(100)
    //스프링 6.x 버전부터 변경된 방식으로, spring security는 자체적으로 준비된 필터들과 동작 순서가 있으며 아래는 그 필터들의 동작유무 및 설정 옵션을 지정하는 역할을 한다.
    public SecurityFilterChain securityFilterChainForView(HttpSecurity httpSecurity) throws Exception {
        String myPattern = "/view/";

        httpSecurity
                // example 업무 체인은 더 낮은 @Order에서 먼저 매칭되므로 여기서는 일반 업무 /view/** 전체를 잡는다.
                .securityMatcher(myPattern + "**")

                // CSRF 비활성화 경로 지정
                .csrf(csrf -> csrf
                        //.ignoringRequestMatchers("/**") // MOTE: 테스트를 편하게 하기 위해 모든 경로 에서 csrf 토큰을 무시할 경우
                        .ignoringRequestMatchers("/noCsrfToken/**")
                )

                // 필요에 따라 추가/삭제 하세요
                .authorizeHttpRequests(authorize ->
                    authorize
                            //로그인 만 되어 있으면 되는 경우
                            .requestMatchers(myPattern + "login/**").authenticated()

                            //authority 체크
                            .requestMatchers(myPattern + "auth-special/**").hasAuthority(AUTH_SPECIAL_FOR_TEST)

                            //role 체크
                            .requestMatchers(myPattern + "role-user/**").hasAnyRole("USER")
                            .requestMatchers(myPattern + "role-system/**").hasAnyRole("SYSTEM")
                            .requestMatchers(myPattern + "role-admin-adminSpecial/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")

                            //method 까지 체크
                            .requestMatchers(HttpMethod.POST, myPattern + "postLogin/**").authenticated()
                            .requestMatchers(HttpMethod.POST, myPattern + "postAuth-special/**").authenticated()
                            .requestMatchers(HttpMethod.POST, myPattern + "postRole-system/**").authenticated()

                            //나머지
                            .anyRequest().permitAll() //일반 대고객 사이트의 경우 기본 페이지들은 모두 permitAll 로 처리
                            //.anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandlerForView)
                        .failureHandler(customAuthenticationFailureHandlerForView)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                );

        return httpSecurity.build();
    }

    @Bean
    @Order(110)
    public SecurityFilterChain securityFilterChainForApi(HttpSecurity httpSecurity) throws Exception {
        String myPattern = "/api/*/"; // *->는 버전 용도임

        httpSecurity
                // example 업무 체인은 더 낮은 @Order에서 먼저 매칭되므로 여기서는 일반 업무 /api/*/** 전체를 잡는다.
                .securityMatcher(myPattern + "**")

                // JWT를 사용하는 경우 CSRF방지 기능을 사용 할 필요가 없음.(CSRF 공격이 쿠키 방식의 session 문제에 기인한 것으로 JWT만 사용하여 세션을 사용하지 않는다면 disable 처리)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // todo: 테스트를 편하게 하기 위해 cors disable 임시 처리
                .cors(AbstractHttpConfigurer::disable)

                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtApiAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtApiAccessDeniedHandler)
                )

                // 필요에 따라 추가/삭제 하세요
                .authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                            //로그인 만 되어 있으면 되는 경우
                            .requestMatchers(myPattern + "login/**").authenticated()

                            //authority 체크
                            .requestMatchers(myPattern + "auth-special/**").hasAuthority(AUTH_SPECIAL_FOR_TEST)

                            //role 체크
                            .requestMatchers(myPattern + "role-user/**").hasAnyRole("USER")
                            .requestMatchers(myPattern + "role-system/**").hasAnyRole("SYSTEM")
                            .requestMatchers(myPattern + "role-admin-adminSpecial/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")

                            //method 까지 체크
                            .requestMatchers(HttpMethod.POST, myPattern + "postLogin/**").authenticated()
                            .requestMatchers(HttpMethod.POST, myPattern + "postAuth-special/**").authenticated()
                            .requestMatchers(HttpMethod.POST, myPattern + "postRole-system/**").authenticated()

                            //나머지
                            .anyRequest().permitAll() //일반 대고객 사이트의 경우 기본 API는 모두 permitAll 로 처리
                            //.anyRequest().authenticated()
                )

                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, jwtApiAuthenticationEntryPoint), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }



}
