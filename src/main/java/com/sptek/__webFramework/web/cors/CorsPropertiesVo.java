package com.sptek.__webFramework.web.cors;

import com.sptek.__webFramework.core.constant.CommonConstants;
import com.sptek.__webFramework.observability.logging.LoggingUtil;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Data
@Component("corsPropertiesVo")
@ConfigurationProperties(prefix = "cors.options")
public class CorsPropertiesVo {
    private String defaultAccessControlAllowOrigin;
    private List<String> accessControlAllowOrigins;
    private String accessControlAllowMethods;
    private String accessControlAllowCredentials;
    private String accessControlMaxAge;
    private String accessControlAllowHeaders;

    @PostConstruct
    public void init() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
        log.info(LoggingUtil.makeBaseForm(CommonConstants.FW_START_LOG_TAG, "CORS Policy Properties"
                ,"defaultAccessControlAllowOrigin: " + accessControlAllowCredentials + "\n"
                        + "accessControlAllowOrigins: " + accessControlAllowOrigins +"\n"
                        + "accessControlAllowMethods: " + accessControlAllowMethods +"\n"
                        + "accessControlAllowCredentials: " + accessControlAllowCredentials +"\n"
                        + "accessControlMaxAge: " + accessControlMaxAge +"\n"
                        + "accessControlAllowHeaders: " + accessControlAllowHeaders
        ));
    }
}
