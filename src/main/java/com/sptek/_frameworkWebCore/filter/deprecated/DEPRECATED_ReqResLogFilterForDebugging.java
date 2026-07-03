package com.sptek._frameworkWebCore.filter.deprecated;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.support.deplicated.DPRECATED_HttpServletRequestWrapperSupport;
import com.sptek._frameworkWebCore.support.deplicated.DEPRECATED_HttpServletResponseWrapperSupport;
import com.sptek._frameworkWebCore.util.*;
import com.sptek._frameworkWebCore.util.RequestUtil;
import com.sptek._frameworkWebCore.util.ResponseUtil;
import com.sptek._frameworkWebCore.util.TypeConvertUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/*
모든  request, response 에 대한 전체 로그를 남긴다. 성능 및 메모리 소묘가 큼으로 로컬에서 개발 디버깅용으로만 사용하여야 한다. 상용 적용 금지.
*/
/**
 * 모든 request/response 내용을 wrapper로 읽어 debug 로그로 남기던 deprecated 필터.
 *
 * <p>성능과 메모리 비용이 크고 응답 body 재기록 책임이 있어 운영 사용 대상이 아니다.
 * 현재 상세 로그는 {@code ReqResDetailLogFilter}와 decision interceptor 조합을 우선 사용한다.</p>
 */
@Slf4j
//@Profile(value = { "local" })
//@Order(3)
//@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
//@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.DEPRECATED_ReqResLogFilterForDebugging", havingValue = "true", matchIfMissing = false)
public class DEPRECATED_ReqResLogFilterForDebugging extends OncePerRequestFilter {

    public DEPRECATED_ReqResLogFilterForDebugging() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * 요청/응답 wrapper를 통해 API와 View 요청의 상세 정보를 로그로 출력한다.
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

        String session = DPRECATEDHttpServletRequestWrapperSupport.getSession().getId();
        String methodType = RequestUtil.getRequestMethodType(DPRECATEDHttpServletRequestWrapperSupport);
        String url = RequestUtil.getRequestUrlQuery(DPRECATEDHttpServletRequestWrapperSupport);
        String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(DPRECATEDHttpServletRequestWrapperSupport, "|"));
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(DPRECATEDHttpServletRequestWrapperSupport));
        String requestBody = DPRECATEDHttpServletRequestWrapperSupport.getRequestBody(); // todo: 컨틀럴러에서 @RequestBody 로 읽은 후에는 가져올수 없기 때문에 필터 체인으로 넘기기 전에 미리 request body를 읽어 둠

        filterChain.doFilter(DPRECATEDHttpServletRequestWrapperSupport, DEPRECATEDHttpServletResponseWrapperSupport);

        String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(DEPRECATEDHttpServletResponseWrapperSupport, "|"));

        if(request.getRequestURI().startsWith("/api/")) {
            String responseBody = DEPRECATEDHttpServletResponseWrapperSupport.getResponseBody();

            String logBody = String.format(
                      "session : %s\n"
                    + "(%s) url : %s\n"
                    + "params : %s\n"
                    + "requestHeader : %s\n"
                    + "requestBody : %s\n"
                    + "responseHeader : %s\n"
                    + "responseBody(%s) : %s\n"
                    , session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , response.getStatus(), StringUtils.hasText(responseBody)? "\n" + responseBody : ""
            );
            log.info(LoggingUtil.makeBaseForm("Request-Response Information caught by the ReqResLogFilterForDebugging", logBody));

        } else {
            String exceptionMsg = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE)).map(Object::toString).orElse("No Exception");
            String responseModelAndView = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW)).map(Object::toString).orElse("");

            String logBody = String.format(
                      "session : %s\n"
                    + "(%s) url : %s\n"
                    + "params : %s\n"
                    + "requestHeader : %s\n"
                    + "requestBody : %s\n"
                    + "responseHeader : %s\n"
                    + "modelAndView(%s) : %s\n"
                    + "exceptionMsg : %s\n"
                    , session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , response.getStatus(), StringUtils.hasText(responseModelAndView)? "\n" + responseModelAndView : ""
                    , exceptionMsg
            );
            log.info(LoggingUtil.makeBaseForm("Request-Response Information caught by the ReqResLogFilterForDebugging", logBody));
        }

        // todo: 중요!! 자신이 response를 HttpServletResponseWrapperSupport로 변환한 최초의 필터라면 response에 body를 최종 write 할 책음을 져야 한다.
        //  (httpServletResponseWrapperSupport가 아닌 response 객체에 써야함)
        if (!(response instanceof DEPRECATED_HttpServletResponseWrapperSupport)) {
            response.getWriter().write(DEPRECATEDHttpServletResponseWrapperSupport.getResponseBody());
        }
    }
}


