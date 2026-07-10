package com.cesco.__projectsCommon.filter;

import com._sptek.__webFramework.security.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Profile(value = { "xxx" })
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
//@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
public class ExampleFilter extends OncePerRequestFilter {

    public ExampleFilter() {
        log.info("{} is Applied.", this.getClass().getSimpleName());
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        //필터 제외 케이스
        if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        // do more what you want ..
        filterChain.doFilter(request, response);
    }

}

