package com._sptek.__webFramework.data.mybatis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 실행 애플리케이션별 MyBatis XML 리소스 위치를 yml에서 주입받는 설정.
 *
 * <p>프레임워크는 SqlSessionFactory 구성 방식만 제공하고, 실제 config/mapper XML의 위치는
 * _webFrameworkExample, _sales, _marketing 같은 실행 단위의 resources에서 지정한다.</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "web-framework.mybatis")
public class MybatisProperties {
    private String configLocationPattern;
    private List<String> mapperLocationPatterns = new ArrayList<>();
}
