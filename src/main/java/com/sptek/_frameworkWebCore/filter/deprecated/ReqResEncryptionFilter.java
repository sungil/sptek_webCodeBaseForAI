package com.sptek._frameworkWebCore.filter.deprecated;

/*
Xss 방지 필터 목적인데.. request param 으로 들어오는 값들에 대한 처리는 필터에서 적용하기가 애매함 (해당 코드는 request body 에만 Xss 필터가 적용됨)
objectMapper 셋팅에서 XssProtectSupport 클레스를 적용하는 방식으로 처리하여 Xss 처리가 중복처리됨(해당 클레스 제거 가능)
컨트롤러 이전단계에서(필터등) request의 stream을 읽어버리면 컨틀롤러에서는 비어있는 request가 넘어가기 때문에 컨트롤러 이전에 request 를 읽은 경우 아래 코드를 참조하도록 남김
*/


import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청/응답 body 변환과 암복호화 필터링을 실험하던 deprecated 필터.
 *
 * <p>현재는 등록 애노테이션이 비활성화되어 런타임 필터 체인에 포함되지 않는다.
 * request stream을 controller 이전에 읽어야 하는 경우 참고용으로만 유지한다.</p>
 */
@Slf4j
//@Order(2)
//@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
//@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.ReqResEncryptionFilter", havingValue = "true", matchIfMissing = false)
public class ReqResEncryptionFilter extends OncePerRequestFilter {

    public ReqResEncryptionFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    /**
     * 현재 구현은 별도 변환 없이 다음 필터로 위임한다.
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);
//        //필터 제외 케이스
//        if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        HttpServletRequestWrapperSupport httpServletRequestWrapperSupport = request instanceof HttpServletRequestWrapperSupport ? (HttpServletRequestWrapperSupport)request : new HttpServletRequestWrapperSupport(request);
//        HttpServletResponseWrapperSupport httpServletResponseWrapperSupport = response instanceof HttpServletResponseWrapperSupport ? (HttpServletResponseWrapperSupport)response : new HttpServletResponseWrapperSupport(response);
//
//        String requestBody = httpServletRequestWrapperSupport.getRequestBody(); //컨트럴러 이전 단계에서 Request 스트림이 읽어졌기 때문에 아래에서 대체 request를 생성해서 넘겨줘야 함
//        if (StringUtils.hasText(requestBody)) {
//            requestBody = requestBody.replace("hello", "hi");
//
//            Map<String, Object> orgJsonObject = new ObjectMapper().readValue(requestBody, HashMap.class);
//            Map<String, Object> newJsonObject = new HashMap<>();
//            orgJsonObject.forEach((key, value) -> newJsonObject.put(key, SecureUtil.charEscape(value.toString())));
//
//            //대체 request를 생성
//            httpServletRequestWrapperSupport.setRequestBody(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(newJsonObject));
//
//        }
//
//        filterChain.doFilter(httpServletRequestWrapperSupport, httpServletResponseWrapperSupport);
//
//        // 원래 응답 데이터 읽기
//        String originalResponseBody = httpServletResponseWrapperSupport.getResponseBody();
//        log.info("Original Response Body: {}", originalResponseBody);
//
//        // 응답 데이터 수정
//        String modifiedResponseBody = originalResponseBody.replaceAll("hi", "changed hi");
//        log.info("Modified Response Body: {}", modifiedResponseBody);
//
//        // 수정된 데이터를 다시 설정
//        httpServletResponseWrapperSupport.setResponseBody(modifiedResponseBody);
//
//        // todo: 중요!! 자신이 response를 HttpServletResponseWrapperSupport로 변환한 최초의 필터라면 response에 body를 최종 write 할 책음을 져야 한다.
//        //  (httpServletResponseWrapperSupport가 아닌 response 객체에 써야함)
//        if (!(response instanceof HttpServletResponseWrapperSupport)) {
//            response.getWriter().write(httpServletResponseWrapperSupport.getResponseBody());
//        }
    }
}

