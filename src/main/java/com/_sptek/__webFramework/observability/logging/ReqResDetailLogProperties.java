package com._sptek.__webFramework.observability.logging;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 요청/응답 상세 로그 필터의 사전 제외 조건을 yml에서 주입받는 설정.
 *
 * <p>필터 초입에서 판단 가능한 path와 요청 content-type을 기준으로 body caching wrapper 적용 여부를 결정한다.
 * 응답 content-type은 컨트롤러 처리 전에는 알 수 없으므로 파일 다운로드/엑셀 export 같은 큰 응답은 path로 제외한다.</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.logging.req-res-detail")
public class ReqResDetailLogProperties {
    private List<String> excludePathPatterns = new ArrayList<>();
    private List<String> excludeContentTypes = new ArrayList<>();
}
