package com._sptek.__webFramework.view.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
@ConfigurationProperties(prefix = "properties-for-model-attributes")
public class PropertiesForModelAttributeVo {
    private Map<String, Object> attributes = new HashMap<>();
}
