package com._sptek.__webFramework.security.authentication.view;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * View form login의 redirect/failure 정책을 yml에서 주입받는 설정.
 *
 * <p>로그인 성공 후 이동하면 안 되는 경로는 업무 프로젝트마다 다를 수 있으므로
 * 프레임워크 코드에 하드코딩하지 않고 프로퍼티로 확장한다.</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.security.view-login")
public class ViewLoginAuthenticationProperties {
    private String defaultTargetUrl = "/";
    private String authenticationFailureCode = "EX_AUTH_FAILED";
    private List<String> notRedirectUrls = new ArrayList<>(List.of(
            "login",
            "/login",
            "/view/login",
            "logout",
            "/logout",
            "/view/logout"
    ));
}
