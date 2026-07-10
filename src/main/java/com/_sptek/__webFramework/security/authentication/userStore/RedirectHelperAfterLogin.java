package com._sptek.__webFramework.security.authentication.userStore;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * View 로그인 성공 후 이동할 URL을 referer, 세션 저장 URL, Spring SavedRequest 기준으로 결정하는 helper.
 *
 * <p>로그인/로그아웃/회원가입처럼 로그인 성공 후 되돌아가면 안 되는 경로는 제외하고,
 * Spring Security가 보관한 SavedRequest가 유효하면 해당 기본 동작을 우선한다.</p>
 */
@Slf4j

// todo: 해당 동작은 기본적으로 링크를 클릭 하여 접속 되는 경우 동작 하며 browser 에 주소를 직접 일력 하는 방식 에서는 동작 하지 않음.
public class RedirectHelperAfterLogin {

    private final static String LOGIN_SUCCESS_REDIRECT_URL = "LOGIN_SUCCESS_REDIRECT_URL";
    private final static String THE_TIME_SPRING_OWN_REDIRECT_URL = "THE_TIME_SPRING_OWN_REDIRECT_URL";

    // 로그인 전 시도한 요청을 세션에 저장 하기 위한 용도 (로그인 전 로그인 필요 페이지 를 클릭 했을때 로그인 페이지 로 이동 하여 로그인 이후 원래의 요청 페이지 로 연결 하기 위함)
    private final static HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

    // todo: 필요시 추가 (로그인 이후 리다이리렉트 되면 안되는 경로)
    private static List<String> NOT_REDIRECT_URLS = List.of(
            "login", "/login", "/view/login",
            "logout", "/logout", "view/logout",
            "signup", "/signup", "/view/signup", "/view/example/authentication/signup", "/view/example/authentication/signupForm"
    );

    /**
     * 로그인 요청의 referer와 Spring SavedRequest를 비교해 최종 redirect URL을 반환한다.
     *
     * <p>null을 반환하면 SavedRequestAwareAuthenticationSuccessHandler의 기본 saved request 처리를 사용한다.</p>
     */
    public static String getRedirectUrlAfterLogging(HttpServletRequest request, HttpServletResponse response) {
        String referer = request.getHeader("referer");
        String refererPath = "";
        String refererQuery = "";
        String redirectParam = "";

        if(referer != null) {
            try {
                URI uri = new URI(referer);
                refererPath = uri.getPath() == null ? "" : uri.getPath(); // 경로 (/ 이후 부분)
                refererQuery = uri.getQuery() == null ? "" : uri.getQuery(); // 쿼리 문자열 (? 이후 부분)

                log.debug("referer uri({}), refererPath({}), refererQuery({})", referer, refererPath, refererQuery);
                redirectParam = refererQuery.isEmpty() ? refererPath : refererPath + "?" + refererQuery;

            } catch (URISyntaxException e) {
                log.debug(e.getMessage());
            }
        }

        //referer 가 없는 경우 (browser 에 url 로 직접 치고 들어온 케이스임, 이전의 요청 으로 인해 남아 있는 LOGIN_SUCCESS_REDIRECT_URL 과 spring 자체 RedirectUrl 정보를 삭제)
        if(refererPath.equals("")) {
            removeRedirectUrlSpringOwn(request, response);
            request.getSession().removeAttribute(LOGIN_SUCCESS_REDIRECT_URL);
            log.debug("came into the login by typing the login url.");
        }

        //로그인 버튼을 클릭해서 들어온 케이스
        if(request.getParameter("button") != null) {
            removeRedirectUrlSpringOwn(request, response);
            log.debug("came into the login by click the login button.");
        }

        //referer 를 로그인 성공시 redirect url로 설정 (단 로그인 페이지에서 로그인 실패 또는 logout 등 이유로.. referer 가 되면 안되는 케이스 는 제외)
        String finalRefererPath = refererPath;
        if (RedirectHelperAfterLogin.NOT_REDIRECT_URLS.stream().noneMatch(url -> url.equals(finalRefererPath))) {
            request.getSession().setAttribute(LOGIN_SUCCESS_REDIRECT_URL, redirectParam);
        }

        String attrLoginSuccessRedirectUrl = Optional.ofNullable(request.getSession().getAttribute(LOGIN_SUCCESS_REDIRECT_URL)).map(Object::toString).orElse("");
        log.debug("helper's redirect url : {}", attrLoginSuccessRedirectUrl);
        log.debug("spring's redirect url : {}", getRedirectUrlSpringOwn(request, response));

        //위 케이스별 처리에도 불고하고 SpringOwn RedirectUrl 이 존재한다면 spring 에게 Redirect 처리를 맞기기 위해 redirectParam 을 null 로 내림 (둘다 없을 경우 springSecurity 가 디폴트 경로로 내림)
        return hasOkRedirectUrlSpringOwn(request, response) ? null :  attrLoginSuccessRedirectUrl;
    }

    /**
     * Spring Security SavedRequest가 로그인 후 이동해도 되는 경로인지 확인한다.
     */
    public static boolean hasOkRedirectUrlSpringOwn(HttpServletRequest request, HttpServletResponse response) {
        //Spring-security는 post 요청에 대해서는 인증 후 saveRequest를 생성하지 않는다.. 보안상 민감할수도 있는 데이터를 session 등에 저장 하지 않기 위해.
        //이걸 custom 클레스에서 overwirte 해서 강제로 savedRequest를 만들수도 있지만... CSRF토큰을 사용하는 경우 최초 form 의 csrf 토큰값이 로그인후 변경되기 때문에 또 문제가 있다.
        //억지로 구현하려하지 말고 가능하면... 이런 한계를 그데로 그데로 받아드리는게 좋을듯 함

        SavedRequest savedRequest =  requestCache.getRequest(request, response);
        String savedRequestRedirectPath = "";

         if(savedRequest != null) {
           String url = savedRequest.getRedirectUrl();
             try {
                 URI uri = new URI(url);
                 savedRequestRedirectPath = uri.getPath() == null ? "" : uri.getPath(); // 경로 (/ 이후 부분)
             } catch (URISyntaxException e) {
                 log.debug(e.getMessage());
             }
         }

        //true 이면 SpringOwn RedirectUrl 로 이동됨
        String finalSavedRequestRedirectPath = savedRequestRedirectPath;
        return (!savedRequestRedirectPath.isEmpty() && RedirectHelperAfterLogin.NOT_REDIRECT_URLS.stream().noneMatch(url -> url.equals(finalSavedRequestRedirectPath)));
    }

    /**
     * Spring Security가 저장한 redirect URL을 조회한다.
     */
    public static String getRedirectUrlSpringOwn(HttpServletRequest request, HttpServletResponse response) {
        return requestCache.getRequest(request, response) == null ? null : requestCache.getRequest(request, response).getRedirectUrl();
    }

    /**
     * Spring Security가 저장한 SavedRequest를 제거한다.
     */
    public static void removeRedirectUrlSpringOwn(HttpServletRequest request, HttpServletResponse response) {
        //request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST"); // todo : 어떤게 더 좋은 방법일까?
        requestCache.removeRequest(request, response);
    }
}
