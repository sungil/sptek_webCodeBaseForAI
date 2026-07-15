package com._sptek.__webFramework.view.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * View 모델에 공통으로 주입할 정적 속성 값을 properties에서 바인딩한다.
 *
 * <p>{@code properties-for-model-attributes.attributes} 하위 값이 존재하면
 * {@link ControllerAdviceForPropertiesToModelAttribute}가 각 View 모델에 동일한 이름으로 값을 추가한다.</p>
 */
@Component
@Data
@ConfigurationProperties(prefix = "properties-for-model-attributes")
public class ViewModelAttributeProperties {
    private Map<String, Object> attributes = new HashMap<>();
}
