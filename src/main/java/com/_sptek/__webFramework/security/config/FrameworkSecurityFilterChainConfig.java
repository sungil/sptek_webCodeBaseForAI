package com._sptek.__webFramework.security.config;

import com._sptek.__webFramework.security.authentication.view.CustomAuthenticationFailureHandlerForView;
import com._sptek.__webFramework.security.authentication.view.CustomAuthenticationSuccessHandlerForView;
import com._sptek.__webFramework.security.jwt.CustomJwtAccessDeniedHandlerForApi;
import com._sptek.__webFramework.security.jwt.CustomJwtAuthenticationEntryPointForApi;
import com._sptek.__webFramework.security.jwt.CustomJwtFilter;
import com._sptek.__webFramework.security.jwt.GeneralTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * 프레임워크 기본 Spring Security filter chain들을 경로와 profile별로 나누어 구성한다.
 *
 * <p>system support, actuator, Swagger/H2, example View/API 같은 프레임워크 제공 경로의 인증/인가 정책을 담당한다.
 * 실제 프로젝트 업무 경로 정책은 _projectCommon의 SecurityFilterChain 확장 지점에서 추가한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class FrameworkSecurityFilterChainConfig {
    private static final String EXAMPLE_AUTH_SPECIAL_FOR_TEST = "AUTH_SPECIAL_FOR_TEST";

    private final CustomAuthenticationSuccessHandlerForView customAuthenticationSuccessHandlerForView;
    private final CustomAuthenticationFailureHandlerForView customAuthenticationFailureHandlerForView;
    private final GeneralTokenProvider generalTokenProvider;
    private final CustomJwtAuthenticationEntryPointForApi customJwtAuthenticationEntryPointForApi;
    private final CustomJwtAccessDeniedHandlerForApi customJwtAccessDeniedHandlerForApi;

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

    public static final String exampleViewPattern = "/view/example/";
    public static final String exampleApiPattern = "/api/*/example/";

    /**
     * 운영 profile에서 Swagger, H2, 예제 경로를 차단한다.
     */
    @Bean
    @Order(10)
    @Profile(value = {"prd"})
    public SecurityFilterChain securityFilterChainForBlockOnPrd(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/swagger-ui.html", "/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**", "/h2-console/**", exampleViewPattern + "**", exampleApiPattern + "**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().denyAll()
                );

        return httpSecurity.build();
    }

    /**
     * dev/stg profile에서 H2와 예제 경로를 차단한다.
     */
    @Bean
    @Order(10)
    @Profile(value = {"dev", "stg"})
    public SecurityFilterChain securityFilterChainForBlockOnStgDev(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher("/h2-console/**", exampleViewPattern + "**", exampleApiPattern + "**")
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




    /**
     * local profile 예제 View 경로의 form login, CSRF, role/authority 샘플 정책을 구성한다.
     */
    @Bean
    @Order(50)
    @Profile(value = {"local"})
    public SecurityFilterChain securityFilterChainForExampleView(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // example view 만 적용
                .securityMatcher(exampleViewPattern + "**")

                // CSRF 비활성화 경로 지정
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/view/example/**") // todo: 테스트를 편하게 하기 위해 모든 경로에서 dsrf 토큰을 무시하도록 임시 처리
                        .ignoringRequestMatchers("/view/example/noCsrfToken/**")
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository()) // 세션과 동일 ttl 이 적용됨 (그럼에도 내가 요청한 폼인지를 확인하는 목적에는 부합됨)
                )

                // todo: session 과 관련된 전반적인 부분을 확인해야함!!!
                // .sessionManagement(session -> session
                //        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션이 필요한 경우에만 생성
                //        .invalidSessionUrl("/session-invalid") // 세션이 유효하지 않으면 이동할 URL 설정
                //        .maximumSessions(1) // 최대 세션 수 설정
                //        .maxSessionsPreventsLogin(true) // 세션 수가 초과되면 로그인 방지
                //        .expiredUrl("/session-expired") //)
                // )

                //path별 Role을 지정함 (controller 의  @PreAuthorize와의 차이점은 여기서 path에 지정하는 방식은 spring security fillter 에 의해 관리되고.. controller 에 지정된 것은 servlet 에서 관리됨)
                //다시말해.. path에 지정하면.. 인증이 필요할때 spring-security-fillter가 로그인 페이지로 자동 이동해 주거나.. 권한이 없을때 filter 레벨에서 403 페이지로 전환해 준다.
                //@PreAuthorize 방식은 인증이 필요한 경우나 권한이 없는경우 관련 EX가 발생되고.. 그에 따른 처리는 Sevlet 내에서 개발자가 알아서 처리해 주어야 한다.(로그인페이지로 자동 연결해주는거 없음)
                //1차적으로 Role에 따라 path를 구분하여 권한처리를 하고 특정 페이지에 조회 기능과 수정 기능이 각각의 별도 권한이 필요하다면 이런 경우는 컨트롤러에 권한 설정을 하는 방식이 적합하지 않을까..
                // todo: 이곳 에서 정의 하는 filter 방식과 Controller 에서 정의 하는 @PreAuthorize 방식의 선택 기준 고민 필요
                .authorizeHttpRequests(authorize ->
                    authorize

                            .requestMatchers(exampleViewPattern + "login/**").authenticated() //로그인 만 되어 있으면 되는 경우
                            .requestMatchers(exampleViewPattern + "auth-special/**").hasAuthority(EXAMPLE_AUTH_SPECIAL_FOR_TEST) //필터 에서 특정 authority 를 직접 확인 하는 케이스+
                            .requestMatchers(exampleViewPattern + "role-user/**").hasAnyRole("USER")
                            .requestMatchers(exampleViewPattern + "role-system/**").hasAnyRole("SYSTEM")
                            .requestMatchers(exampleViewPattern + "role-admin-adminSpecial/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")

                            .requestMatchers(HttpMethod.POST, exampleViewPattern + "postLogin/**").authenticated()
                            .requestMatchers(HttpMethod.POST, exampleViewPattern + "postAuth-special/**").authenticated()
                            .requestMatchers(HttpMethod.POST, exampleViewPattern + "postRole-system/**").authenticated()

                            .anyRequest().permitAll()
                            //.anyRequest().authenticated()
                )

                // .httpBasic(Customizer.withDefaults()) //얼럿창형
                // .formLogin(withDefaults()) //form형 디폴트 로그인 (--:8443/login 으로 고정되어 있는듯 8443 포트에서만 정상 동작됨)
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .loginProcessingUrl("/view/loginProcess")
                        //.defaultSuccessUrl("/")
                        .successHandler(customAuthenticationSuccessHandlerForView)
                        .failureHandler(customAuthenticationFailureHandlerForView)
                )
                // View 로그인 처리
                // 로그인 폼에서 정보 입력 후 <form action=/view/login> 으로 하면 서버의 AuthenticationManager 로 입력 정보가 자동 으로 전달됨
                // 전달 받은 로그인 정보를 보고 어떤 타입의 로그인 처리 인지 확인후 등록된 AuthenticationProvider(CustomAuthenticationProvider)의 support 타입을 보고 그에 맞는 대상에 전달됨(security 내부 적으로 처리됨)
                // AuthenticationProvider(CustomAuthenticationProvider) 에서 관련 처리를 하고(개발자) 리턴 타입을 맟춰 응답 하면 AuthenticationManager SecurityContextHolder 및 session 에 관련 처리를 함
                // security 설정에 따라 다르지 만 이후 요청이 들어 오면 SecurityFilterChain 에소 세션(쿠키)을 기준 으로 SecurityContextHolder 다시 가져 와서 로그인 상태를 유지함
                // 세션에 SecurityContextHolder 가 없거나 만료 되었 다면.. 그에 따른 후속 처리 진행

                // view 로그 아웃 처리
                // 1. SecurityContext에 저장된 인증 정보 제거
                // 2. 기본 적으로 JSESSIONID 쿠키를 삭제
                // 3. /view/login?logout 으로 redirect 처리 (logoutSuccessHandler 추가 시에는 logoutSuccessHandler 에서 해줘야 함)
                .logout(logout -> logout
                        // 로그아웃 처리 url 설정 (해당 req 매핑이 존재할 필요는 없음)
                        .logoutUrl("/view/logout")

                        // 추가적인 로직이 필요한 경우
                        //.logoutSuccessHandler((request, response, authentication) -> {
                        //    log.debug("User has logged out: " + (authentication != null ? authentication.getName() : "Anonymous"));
                        //    response.sendRedirect("/view/login?logout"); // custom 코드를 넣었다면 마지막 리다이렉션 처리까지 직접 해줘야함.
                        //})

                );

        return httpSecurity.build();
    }

    /**
     * local profile 예제 API 경로의 JWT 필터와 샘플 role/authority 정책을 구성한다.
     */
    @Bean
    @Order(60)
    @Profile(value = {"local"})
    public SecurityFilterChain securityFilterChainForExampleApi(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // api example 적용
                .securityMatcher(exampleApiPattern + "**")

                // JWT를 사용하는 경우 CSRF방지 기능을 사용 할 필요가 없음.(CSRF 공격이 쿠키 방식의 session 문제에 기인한 것으로 JWT만 사용하여 세션을 사용하지 않는다면 disable 처리)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                                // 필요시 추가해 나감.
                                .requestMatchers(exampleApiPattern + "login/**").authenticated() //로그인 만 되어 있으면 되는 경우
                                .requestMatchers(exampleApiPattern + "auth-special/**").hasAuthority(EXAMPLE_AUTH_SPECIAL_FOR_TEST) //필터 에서 특정 authority 를 직접 확인 하는 케이스+
                                .requestMatchers(exampleApiPattern + "role-user/**").hasAnyRole("USER")
                                .requestMatchers(exampleApiPattern + "role-system/**").hasAnyRole("SYSTEM")
                                .requestMatchers(exampleApiPattern + "role-admin-adminSpecial/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")

                                .requestMatchers(HttpMethod.POST, exampleApiPattern + "postLogin/**").authenticated()
                                .requestMatchers(HttpMethod.POST, exampleApiPattern + "postAuth-special/**").authenticated()
                                .requestMatchers(HttpMethod.POST, exampleApiPattern + "postRole-system/**").authenticated()

                                .anyRequest().permitAll()
                                //.requestMatchers("/api/*/example/signup", "/api/*/example/login", "/api/*/example/logout").permitAll()
                )

                // Security filter chain 내부의 인증/인가 실패는 CustomErrorController로 가지 않을 수 있어 API 공통 응답 handler를 직접 연결함
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(customJwtAuthenticationEntryPointForApi)
                                .accessDeniedHandler(customJwtAccessDeniedHandlerForApi)
                )

                // security와 관련해서 custom하게 만든 필터가 있다면 적정 위치에 추가할 수 있다.
                // UsernamePasswordAuthenticationFilter 은 스프링 자체 필터로, post 방식, {loginProcessingUrl} 경로 요청시 동작하며
                // 해당 POST request로 전달된 정보를 이용해 스프링의 authenticationManager 통한 인증 절차를 요청함
                // jwt 방식일 경우(일반적으로 브라우저가 아닌 클라이언트의 API호출) UsernamePasswordAuthenticationFilter 에서는 Bearer token 인증을 처리할 수 없음
                // 그래서 그 앞에 CustomJwtFilter를 두어 Authorization: Bearer 값이 있는 요청을 Authentication으로 변환함
                // API 체인은 STATELESS이므로 View 로그인 세션이 있더라도 이 체인에서는 세션 기반 SecurityContext를 인증 근거로 사용하지 않음
                .addFilterBefore(new CustomJwtFilter(generalTokenProvider, customJwtAuthenticationEntryPointForApi), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
