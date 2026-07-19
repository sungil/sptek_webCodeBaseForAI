package com._sptek._webFrameworkExample.config;

import com._sptek.__webFramework.security.authentication.view.CustomAuthenticationFailureHandlerForView;
import com._sptek.__webFramework.security.authentication.view.CustomAuthenticationSuccessHandlerForView;
import com._sptek.__webFramework.security.jwt.CustomJwtAccessDeniedHandlerForApi;
import com._sptek.__webFramework.security.jwt.CustomJwtAuthenticationEntryPointForApi;
import com._sptek.__webFramework.security.jwt.CustomJwtFilter;
import com._sptek.__webFramework.security.jwt.GeneralTokenProvider;
import com._sptek._webFrameworkExample.unit.authentication.authorization.AuthorityEnum;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * _webFrameworkExample 업무 프로젝트의 예제 View/API 경로 보안 정책을 구성한다.
 *
 * <p>프레임워크는 form login, JWT handler, 공통 principal 같은 보안 부품을 제공하고,
 * 이 클래스는 예제 업무 URL에 어떤 Role/Authority가 필요한지 결정한다. 업무 URL 정책이
 * __webFramework 안으로 들어가지 않도록 분리하는 것이 이 설정의 주된 책임이다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ExampleSecurityFilterChainConfig {
    private static final String EXAMPLE_VIEW_PATTERN = "/view/example/";
    private static final String EXAMPLE_API_PATTERN = "/api/*/example/";
    private static final String AUTH_SPECIAL_FOR_TEST = AuthorityEnum.AUTH_SPECIAL_FOR_TEST.name();

    private final CustomAuthenticationSuccessHandlerForView customAuthenticationSuccessHandlerForView;
    private final CustomAuthenticationFailureHandlerForView customAuthenticationFailureHandlerForView;
    private final GeneralTokenProvider generalTokenProvider;
    private final CustomJwtAuthenticationEntryPointForApi customJwtAuthenticationEntryPointForApi;
    private final CustomJwtAccessDeniedHandlerForApi customJwtAccessDeniedHandlerForApi;

    /**
     * 운영 profile에서 예제 업무 View/API 경로를 차단한다.
     */
    @Bean
    @Order(12)
    @Profile(value = {"prd"})
    public SecurityFilterChain securityFilterChainForExampleBlockOnPrd(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher(EXAMPLE_VIEW_PATTERN + "**", EXAMPLE_API_PATTERN + "**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().denyAll()
                );

        return httpSecurity.build();
    }

    /**
     * dev/stg profile에서 예제 업무 View/API 경로를 차단한다.
     */
    @Bean
    @Order(12)
    @Profile(value = {"dev", "stg"})
    public SecurityFilterChain securityFilterChainForExampleBlockOnStgDev(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher(EXAMPLE_VIEW_PATTERN + "**", EXAMPLE_API_PATTERN + "**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().denyAll()
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
                .securityMatcher(EXAMPLE_VIEW_PATTERN + "**")
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/view/example/**")
                        .ignoringRequestMatchers("/view/example/noCsrfToken/**")
                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(EXAMPLE_VIEW_PATTERN + "login/**").authenticated()
                        .requestMatchers(EXAMPLE_VIEW_PATTERN + "auth-special/**").hasAuthority(AUTH_SPECIAL_FOR_TEST)
                        .requestMatchers(EXAMPLE_VIEW_PATTERN + "role-user/**").hasAnyRole("USER")
                        .requestMatchers(EXAMPLE_VIEW_PATTERN + "role-system/**").hasAnyRole("SYSTEM")
                        .requestMatchers(EXAMPLE_VIEW_PATTERN + "role-admin-adminSpecial/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")
                        .requestMatchers(HttpMethod.POST, EXAMPLE_VIEW_PATTERN + "postLogin/**").authenticated()
                        .requestMatchers(HttpMethod.POST, EXAMPLE_VIEW_PATTERN + "postAuth-special/**").authenticated()
                        .requestMatchers(HttpMethod.POST, EXAMPLE_VIEW_PATTERN + "postRole-system/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .loginProcessingUrl("/view/loginProcess")
                        .successHandler(customAuthenticationSuccessHandlerForView)
                        .failureHandler(customAuthenticationFailureHandlerForView)
                )
                .logout(logout -> logout
                        .logoutUrl("/view/logout")
                );

        return httpSecurity.build();
    }

    /**
     * local profile 예제 API 경로의 JWT 필터와 샘플 role/authority 정책을 구성한다.
     *
     * <p>API 체인은 STATELESS이므로 View 로그인 세션이 있더라도 이 체인에서는 세션 기반
     * SecurityContext를 인증 근거로 사용하지 않고, Authorization Bearer token으로 principal을 복원한다.</p>
     */
    @Bean
    @Order(60)
    @Profile(value = {"local"})
    public SecurityFilterChain securityFilterChainForExampleApi(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .securityMatcher(EXAMPLE_API_PATTERN + "**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .requestMatchers(EXAMPLE_API_PATTERN + "login/**").authenticated()
                        .requestMatchers(EXAMPLE_API_PATTERN + "auth-special/**").hasAuthority(AUTH_SPECIAL_FOR_TEST)
                        .requestMatchers(EXAMPLE_API_PATTERN + "role-user/**").hasAnyRole("USER")
                        .requestMatchers(EXAMPLE_API_PATTERN + "role-system/**").hasAnyRole("SYSTEM")
                        .requestMatchers(EXAMPLE_API_PATTERN + "role-admin-adminSpecial/**").hasAnyRole("ADMIN", "ADMIN_SPECIAL")
                        .requestMatchers(HttpMethod.POST, EXAMPLE_API_PATTERN + "postLogin/**").authenticated()
                        .requestMatchers(HttpMethod.POST, EXAMPLE_API_PATTERN + "postAuth-special/**").authenticated()
                        .requestMatchers(HttpMethod.POST, EXAMPLE_API_PATTERN + "postRole-system/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customJwtAuthenticationEntryPointForApi)
                        .accessDeniedHandler(customJwtAccessDeniedHandlerForApi)
                )
                .addFilterBefore(new CustomJwtFilter(generalTokenProvider, customJwtAuthenticationEntryPointForApi), UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
