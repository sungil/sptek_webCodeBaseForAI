package com._sptek.__webFramework.bootstrap.startup;

import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.observability.logging.Enable_GlobalEnvLog_At_Main;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 애플리케이션 시작 시 현재 Spring Environment의 주요 프로퍼티를 로그로 출력하는 startup logger.
 *
 * <p>메인 클래스에 {@link Enable_GlobalEnvLog_At_Main}이 붙은 경우에만 동작한다.
 * 민감 정보로 판단한 값은 앞 3자만 남기고 나머지를 마스킹한다.</p>
 */
@Slf4j
@Component
public class StartupEnvironmentLogger {
    private static final int SENSITIVE_VALUE_VISIBLE_PREFIX_LENGTH = 3;
    private static final String MASKED_VALUE_SUFFIX = "****";

    /**
     * 메인 클래스 애노테이션 레지스트리 초기화 후 환경 로그 활성화 여부를 확인하고 출력한다.
     */
    public void logIfEnabled(ApplicationContext applicationContext) {
        if (MainClassAnnotationRegister.hasAnnotation(Enable_GlobalEnvLog_At_Main.class)) {
            logEnvironment(applicationContext);
        }
    }

    private void logEnvironment(ApplicationContext applicationContext) {
        Environment environment = applicationContext.getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();
        StringBuilder globalEnvironment = new StringBuilder(String.format("activeProfiles : %s%n", Arrays.toString(activeProfiles)));
        Pattern sensitiveKeyPattern = buildSensitiveKeyPattern();
        MutablePropertySources mutablePropertySources = ((AbstractEnvironment) environment).getPropertySources();

        StreamSupport.stream(mutablePropertySources.spliterator(), false)
                .filter(propertySources -> propertySources instanceof EnumerablePropertySource)
                .map(propertySources -> ((EnumerablePropertySource<?>) propertySources).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .forEach(propertyName -> appendProperty(environment, sensitiveKeyPattern, globalEnvironment, propertyName));

        String logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_GlobalEnvLog_At_Main.class).get("value"), "");
        log.info(LoggingUtil.makeBaseForm(logTag, "Global Environment", globalEnvironment.toString()));
    }

    private void appendProperty(Environment environment, Pattern sensitiveKeyPattern, StringBuilder globalEnvironment, String propertyName) {
        String rawValue = environment.getProperty(propertyName);
        String displayValue = isSensitiveProperty(propertyName, sensitiveKeyPattern) ? maskSensitiveValue(rawValue) : String.valueOf(rawValue);
        globalEnvironment.append(String.format("%s : %s%n", propertyName, displayValue));
    }

    private String maskSensitiveValue(String value) {
        if (value == null || value.isEmpty()) return MASKED_VALUE_SUFFIX;
        if (value.length() <= SENSITIVE_VALUE_VISIBLE_PREFIX_LENGTH) return MASKED_VALUE_SUFFIX;
        return value.substring(0, SENSITIVE_VALUE_VISIBLE_PREFIX_LENGTH) + MASKED_VALUE_SUFFIX;
    }

    private boolean isSensitiveProperty(String propertyName, Pattern sensitiveKeyPattern) {
        return propertyName != null && sensitiveKeyPattern.matcher(propertyName).matches();
    }

    private Pattern buildSensitiveKeyPattern() {
        String keywordRegex = getSensitiveKeywords().stream()
                .filter(Objects::nonNull)
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));
        return Pattern.compile("(?i).*(" + keywordRegex + ").*");
    }

    private Set<String> getSensitiveKeywords() {
        return Set.of(
                "authorization",
                "auth",
                "bearer",
                "credential",
                "credentials",
                "password",
                "passwd",
                "pwd",
                "secret",
                "token",
                "jwt",
                "oauth",
                "saml",
                "session",
                "cookie",
                "api-key",
                "apikey",
                "api_key",
                "access-key",
                "access_key",
                "accesskey",
                "access-token",
                "access_token",
                "refresh-token",
                "refresh_token",
                "private-key",
                "private_key",
                "privatekey",
                "public-key",
                "public_key",
                "publickey",
                "client-secret",
                "client_secret",
                "clientsecret",
                "key",
                "salt",
                "hash",
                "encrypt",
                "decrypt",
                "cipher",
                "ssl",
                "tls",
                "keystore",
                "key-store",
                "key_store",
                "truststore",
                "trust-store",
                "trust_store",
                "certificate",
                "cert",
                "pem",
                "p12",
                "pfx",
                "jks",
                "rsa",
                "dsa",
                "ecdsa",
                "pgp",
                "ssh",
                "wallet",
                "license"
        );
    }
}
