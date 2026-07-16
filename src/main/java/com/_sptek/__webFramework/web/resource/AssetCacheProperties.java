package com._sptek.__webFramework.web.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code /assets/**} 영역에 적용할 HTTP cache header 정책을 yml에서 주입받는 설정.
 *
 * <p>이 설정은 파일명 변경이나 JS/CSS 내부 참조 변환을 수행하지 않는다. 장기 캐시 안전성은
 * content hash 파일명, 배포 시 파일명 변경, CDN purge 같은 asset 운영 정책으로 보장해야 한다.</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.assets.cache")
public class AssetCacheProperties {
    private String pathPattern = "/assets/**";
    private List<String> locations = new ArrayList<>(List.of(
            "classpath:/static/assets/"
    ));
    private Duration maxAge = Duration.ofDays(365);
    private boolean publicCache = true;
    private boolean immutable = true;
}
