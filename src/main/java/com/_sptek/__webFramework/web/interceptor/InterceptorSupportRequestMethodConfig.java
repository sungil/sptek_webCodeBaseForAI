package com._sptek.__webFramework.web.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

/*/
인터셉터가 특정 메소드(GET, POST, PUT, DELETE 등)를 구분해서 동작해야 하는 경우 InterceptorMatchSupport 를 통해 해당 인터셉터를 등록하도록 한다.
물론!! 인터셉터 내부에서 request.getMetho() 를 통해 구분된 동작을 처리할 수 있으나 인터셉터는 별도의 config 파일에 동작을 명시하고 있는 방식임으로 일관성을 갖게 처리하기 위함
 */
/**
 * 기존 HandlerInterceptor에 HTTP method 조건을 추가로 적용하는 wrapper interceptor.
 *
 * <p>Spring MVC의 addPathPatterns/excludePathPatterns는 path 중심이므로,
 * 같은 path라도 GET/POST 같은 method 조건을 config 코드에서 함께 표현하고 싶을 때 사용한다.</p>
 */
@Slf4j
public class InterceptorSupportRequestMethodConfig implements HandlerInterceptor {
    private final HandlerInterceptor handlerInterceptor;
    private final MatchInfoContainer matchInfoContainer;

    public InterceptorSupportRequestMethodConfig(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
        this.matchInfoContainer = new MatchInfoContainer();
    }

    /**
     * 현재 요청이 include/exclude method 조건을 통과한 경우에만 실제 interceptor로 위임한다.
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        if (matchInfoContainer.isMatchedRequest(request)) {
            return handlerInterceptor.preHandle(request, response, handler);
        }
        return true;
    }

    /**
     * 특정 path와 HTTP method 조합은 실제 interceptor 호출 대상에 포함한다.
     */
    public InterceptorSupportRequestMethodConfig includePathPattern(String pathPattern, HttpMethod pathMethod) {
        matchInfoContainer.includePathPattern(pathPattern, pathMethod);
        return this;
    }

    /**
     * 특정 path와 HTTP method 조합은 실제 interceptor 호출 대상에서 제외한다.
     */
    public InterceptorSupportRequestMethodConfig excludePathPattern(String pathPattern, HttpMethod pathMethod) {
        matchInfoContainer.excludePathPattern(pathPattern, pathMethod);
        return this;
    }

    /**
     * path pattern과 HTTP method 조건을 함께 보관하는 값 객체.
     */
    @Data
    @AllArgsConstructor
    public class MatchInfo {
        private String path;
        private HttpMethod method;
    }

    /**
     * include/exclude method 조건 목록을 관리하고 현재 request와의 매칭 여부를 계산한다.
     */
    public class MatchInfoContainer {
        private final AntPathMatcher pathMatcher;
        private final List<MatchInfo> includeMatchInfos;
        private final List<MatchInfo> excludeMatchInfos;

        public MatchInfoContainer() {
            this.pathMatcher = new AntPathMatcher();
            this.includeMatchInfos = new ArrayList<>();
            this.excludeMatchInfos = new ArrayList<>();
        }

        /**
         * include에 명시적으로 걸리거나 exclude에 걸리지 않으면 interceptor를 실행한다.
         */
        public boolean isMatchedRequest(HttpServletRequest request) {
            boolean includeMatchResult = includeMatchInfos.stream()
                    .anyMatch(matchInfo -> anyMatchPathPattern(request, matchInfo));

            boolean excludeMatchResult = excludeMatchInfos.stream()
                    .anyMatch(matchInfo -> anyMatchPathPattern(request, matchInfo));

            log.debug("includeMatchResult={}, excludeMatchResult={}", includeMatchResult, excludeMatchResult);
            return includeMatchResult || !excludeMatchResult;
        }

        /**
         * request servlet path와 method가 지정한 조건과 모두 맞는지 확인한다.
         */
        private boolean anyMatchPathPattern(HttpServletRequest request, MatchInfo matchInfo) {
            return pathMatcher.match(matchInfo.getPath(), request.getServletPath()) &&
                    matchInfo.getMethod().matches(request.getMethod());
        }

        /**
         * include 조건을 추가한다.
         */
        public void includePathPattern(String includePath, HttpMethod includeMethod) {
            this.includeMatchInfos.add(new MatchInfo(includePath, includeMethod));
        }

        /**
         * exclude 조건을 추가한다.
         */
        public void excludePathPattern(String excludePath, HttpMethod excludeMethod) {
            this.excludeMatchInfos.add(new MatchInfo(excludePath, excludeMethod));
        }
    }

}


