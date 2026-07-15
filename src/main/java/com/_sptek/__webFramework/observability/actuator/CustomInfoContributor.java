package com._sptek.__webFramework.observability.actuator;

import com._sptek.__webFramework.application.info.ApplicationInfoProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component

public class CustomInfoContributor implements InfoContributor {
    private final ApplicationInfoProperties applicationInfoProperties;

    @Override
    public void contribute(Info.Builder builder) {
        if (applicationInfoProperties.getApp() == null) {
            builder.withDetail("app", Map.of());
            return;
        }

        builder.withDetail("app", Map.of(
                "name", applicationInfoProperties.getApp().getName(),
                "version", applicationInfoProperties.getApp().getVersion(),
                "description", applicationInfoProperties.getApp().getDescription()
        ));
    }
}
