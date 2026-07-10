package com._sptek.__webFramework.web.cors;

import com._sptek.__webFramework.web.filter.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import com._sptek.__webFramework.core.constant.CommonConstants;
import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.security.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * 프레임워크 CORS 정책 설정을 API 요청에 적용하는 필터.
 *
 * <p>Spring Security의 기본 CORS 처리 대신 프로젝트 설정값을 직접 읽어 응답 헤더를 구성한다.
 * preflight OPTIONS 요청은 이 필터에서 바로 OK 응답으로 마무리한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
//@Profile(value = { "local", "dev", "stg", "prd" })
//@HasAnnotationOnMain_InBean(EnableCorsPolicyFilter_InMain.class)
//@WebFilter(urlPatterns = "/api/*") //브라우저에서 실제 CORS 확인 처리는 api 호출때 주로 요첨 됨으로..
public class CorsPolicyFilter extends OncePerRequestFilter {
    // CORS 설정은 SpringSecurity 에서 설정 가능 하나..
    // 상세한 컨트롤 및 어노테이션을 통한 사용 설정을 위해 개별 필터로 처리함(SpringSecurity 의 CORS 디폴트 처리는 disabled 처리함)

    private final CorsPropertiesVo corsPropertiesVo;

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * Origin 요청 헤더가 있으면 허용 origin을 계산해 CORS 응답 헤더를 설정한다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)) {
            // todo: NotEssentialRequest 에 대해 필터 제외 케이스 를 적용하는게 맞을까? 보안 협의가 필요
            if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String origin = Collections.list(request.getHeaderNames()).stream()
                .filter(headerName -> headerName.equalsIgnoreCase("Origin"))
                .findFirst() // Origin 헤더의 첫번째 값을 사용함, 기본적으로 Origin은 하나만 있어야 함
                .map(request::getHeader)
                .orElse("NoOrigin");

        // 브라우저는 요청 형태에 따라 다양한 CORS 정책을 사용함
        // ex: GET 일때는 Option 요청을 보내지 않고 본래 요청에 Origin 만 넣어서 보냄, POST 등 중요한? 요청의 경우 Option 을 먼저 보내고 안전 한지 확인 후 본래 요청 에도 Origin 을 넣어 보냄)
        if (!origin.equalsIgnoreCase("NoOrigin")) {
            String allowOrigin = "*".equals(corsPropertiesVo.getDefaultAccessControlAllowOrigin()) || corsPropertiesVo.getAccessControlAllowOrigins().contains(origin)
                    ? origin
                    : corsPropertiesVo.getDefaultAccessControlAllowOrigin();

            response.setHeader("Access-Control-Allow-Origin", allowOrigin);
            response.setHeader("Access-Control-Allow-Methods", corsPropertiesVo.getAccessControlAllowMethods());
            response.setHeader("Access-Control-Allow-Headers", corsPropertiesVo.getAccessControlAllowHeaders());
            response.setHeader("Access-Control-Allow-Credentials", corsPropertiesVo.getAccessControlAllowCredentials());
            response.setHeader("Access-Control-Max-Age", corsPropertiesVo.getAccessControlMaxAge());
            log.debug(origin.equals(allowOrigin) ? "CORS policy validation passed." : "CORS policy validation denied.");
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            //Option 요청 일때는 바로 응답 처리 하고 끝냄
            response.setStatus(HttpServletResponse.SC_OK);

        } else {
            filterChain.doFilter(request, response); // 다른 요청은 그대로 통과
        }
    }
}



