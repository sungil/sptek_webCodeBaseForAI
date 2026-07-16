package com._sptek.__webFramework.web.cors;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "cors.options")
/**
 * 프레임워크 CORS 필터가 사용할 허용 Origin, Method, Header 정책을 바인딩한다.
 *
 * <p>프로젝트 profile 설정에서 값을 주입받으며, credential 허용 상태에서 전체 Origin({@code *})을
 * 열어두는 조합처럼 브라우저 CORS 정책과 충돌하거나 보안상 위험한 설정은 시작 시점에 차단한다.</p>
 */
public class CorsProperties {
    private List<String> allowedOrigins = new ArrayList<>();
    private List<String> allowedMethods = new ArrayList<>();
    private List<String> allowedHeaders = new ArrayList<>();
    private boolean allowCredentials;
    private long maxAge;

    @PostConstruct
    public void init() {
        validate();

        log.info(LoggingConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
        log.info(LoggingUtil.makeBaseForm(LoggingConstants.FW_START_LOG_TAG, "CORS Policy Properties"
                , "allowedOrigins: " + allowedOrigins + "\n"
                        + "allowedMethods: " + allowedMethods + "\n"
                        + "allowedHeaders: " + allowedHeaders + "\n"
                        + "allowCredentials: " + allowCredentials + "\n"
                        + "maxAge: " + maxAge
        ));
    }

    public boolean allowsAllOrigins() {
        return allowedOrigins.contains("*");
    }

    public boolean isOriginAllowed(String origin) {
        return allowsAllOrigins() || allowedOrigins.contains(origin);
    }

    private void validate() {
        if (allowedOrigins.isEmpty()) {
            throw new IllegalStateException("cors.options.allowed-origins must not be empty.");
        }
        if (allowedMethods.isEmpty()) {
            throw new IllegalStateException("cors.options.allowed-methods must not be empty.");
        }
        if (allowedHeaders.isEmpty()) {
            throw new IllegalStateException("cors.options.allowed-headers must not be empty.");
        }
        if (allowCredentials && allowsAllOrigins()) {
            throw new IllegalStateException("cors.options.allow-credentials=true cannot be used with allowed-origins=*.");
        }
    }
}
