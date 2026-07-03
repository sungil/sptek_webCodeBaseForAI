package com.sptek._frameworkWebCore.filter.deprecated;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import java.io.IOException;

//@Profile(value = { "xxx" }) //우선 항상 사용하지 않는 것으로
/**
 * API GET 응답에 Shallow ETag를 적용하던 deprecated 필터 설정.
 *
 * <p>{@code @Configuration}이 비활성화되어 현재 런타임에는 등록되지 않는다.
 * ETag 적용은 호출자가 If-None-Match 헤더를 다시 보내는 캐시 흐름까지 함께 검토해야 한다.</p>
 */
@Slf4j
//@Configuration
//@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.EtagFilter", havingValue = "true", matchIfMissing = false)
public class EtagFilterConfig {

    /*
    <좀더 확인 확인 필요>
    해당 필터를 적용하여 response header 에 Etag 가 있더라도
    실제 304(Not Modify) 응답을 받기 위해서는 호출하는쪽에서(브라우저, ajax, fetch..)
    request header의 If-None-Match 값으로 다시 Etag값을 넘겨 줄수 있어야 한다.
    */

    /**
     * 활성화된 경우 /api 하위 GET 요청에 ShallowEtagHeaderFilter를 제한적으로 적용한다.
     */
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> shallowEtagHeaderFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + "EtagFilter is Applied.");

        FilterRegistrationBean<OncePerRequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(
                new OncePerRequestFilter() {
                    private final ShallowEtagHeaderFilter shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
                    @Override
                    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException, ServletException, ServletException {
                        //Etag response 해더 조건
                        if ("GET".equalsIgnoreCase(request.getMethod()) && request.getRequestURI().matches("/api/.*")) {
                            log.debug("shallowEtagHeaderFilter applied for this request");
                            shallowEtagHeaderFilter.doFilter(request, response, filterChain);
                        } else {
                            filterChain.doFilter(request, response);
                        }
                    }
                });
        //todo : /**로 해야 할것 같은데 /* 로 해야 적용되는 이유 확인 필요
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
