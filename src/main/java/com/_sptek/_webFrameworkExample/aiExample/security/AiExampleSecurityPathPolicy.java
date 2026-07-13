package com._sptek._webFrameworkExample.aiExample.security;

import org.springframework.http.HttpMethod;

public final class AiExampleSecurityPathPolicy {
    public static final String API_PATTERN = "/api/ai-example/**";
    public static final String VIEW_PATTERN = "/view/ai-example/**";
    public static final HttpMethod WRITE_METHOD = HttpMethod.POST;

    private AiExampleSecurityPathPolicy() {
    }
}
