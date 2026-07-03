package com.sptek._frameworkWebCore.filter.deprecated;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.support.deplicated.DPRECATED_HttpServletRequestWrapperSupport;
import com.sptek._frameworkWebCore.support.deplicated.DEPRECATED_HttpServletResponseWrapperSupport;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


/**
 * 요청/응답 객체를 deprecated wrapper 타입으로 변환하던 필터.
 *
 * <p>현재 활성 필터에서는 Spring의 ContentCaching wrapper 또는 전용 support를 사용하므로
 * 이 필터는 과거 구현 참고용으로만 유지한다.</p>
 */
@Slf4j
//@Order(1) //httpServletResponseWrapperSupport 형태가 최종 response 형태로 나가야 함으로 필터의 마지막에 처리되야함(마지막 처리를 위해선 가장 먼저 저일되야 함)
//@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
//@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.DEPRECATED_ConvertReqResToReqResWrapperFilter", havingValue = "true", matchIfMissing = false)
public class DEPRECATED_ConvertReqResToReqResWrapperFilter extends OncePerRequestFilter {

    public DEPRECATED_ConvertReqResToReqResWrapperFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * 요청과 응답을 deprecated wrapper로 감싼 뒤 최종 응답 body를 원 response에 다시 기록한다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        //필터 제외 케이스
        if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        DPRECATED_HttpServletRequestWrapperSupport DPRECATEDHttpServletRequestWrapperSupport = request instanceof DPRECATED_HttpServletRequestWrapperSupport ? (DPRECATED_HttpServletRequestWrapperSupport)request : new DPRECATED_HttpServletRequestWrapperSupport(request);
        DEPRECATED_HttpServletResponseWrapperSupport DEPRECATEDHttpServletResponseWrapperSupport = response instanceof DEPRECATED_HttpServletResponseWrapperSupport ? (DEPRECATED_HttpServletResponseWrapperSupport)response : new DEPRECATED_HttpServletResponseWrapperSupport(response);
        filterChain.doFilter(DPRECATEDHttpServletRequestWrapperSupport, DEPRECATEDHttpServletResponseWrapperSupport);

        // todo: 중요!! 자신이 response를 HttpServletResponseWrapperSupport로 변환한 최초의 필터라면 response에 body를 최종 write 할 책음을 져야 한다.
        //  (httpServletResponseWrapperSupport가 아닌 response 객체에 써야함)
        if (!(response instanceof DEPRECATED_HttpServletResponseWrapperSupport)) {
            response.getWriter().write(DEPRECATEDHttpServletResponseWrapperSupport.getResponseBody());
        }

    }
}

