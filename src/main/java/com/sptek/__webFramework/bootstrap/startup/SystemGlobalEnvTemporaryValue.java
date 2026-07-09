package com.sptek.__webFramework.bootstrap.startup;

import com.sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com.sptek.__webFramework.observability.logging.Enable_GlobalEnvLog_At_Main;
import com.sptek.__webFramework.observability.logging.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j

public class SystemGlobalEnvTemporaryValue {
    // 해당 프로젝트에 적용되는 거의 모든 환견 설정 값을 보여 준다.
    // 민감 정보를 포함하기 때문에 @Enable_GlobalEnvLogging_At_Main 가 존재하는 경우만 동작하게 처리함
    // 단순 로깅만 처리함 (내부 변수만 사용하여 민감 정보에 대한 GC 가 바로 이루어 지도록 처리)

    public SystemGlobalEnvTemporaryValue(ApplicationContext applicationContext) {
        if (MainClassAnnotationRegister.hasAnnotation(Enable_GlobalEnvLog_At_Main.class)) {
            Environment environment = applicationContext.getEnvironment();
            String[] activeProfiles = environment.getActiveProfiles();
            StringBuffer globalEnvironment = new StringBuffer(String.format("activeProfiles : %s%n", Arrays.toString(activeProfiles)));

            String keywordRegex = getSensitiveKeyword().stream()
                    .filter(Objects::nonNull)
                    .map(Pattern::quote)
                    .collect(Collectors.joining("|"));

            final Pattern sensitiveKeyPattern = Pattern.compile("(?i).*(" + keywordRegex + ").*");
            final MutablePropertySources mutablePropertySources = ((AbstractEnvironment) environment).getPropertySources();

            StreamSupport.stream(mutablePropertySources.spliterator(), false)
                    .filter(propertySources -> propertySources instanceof EnumerablePropertySource)
                    .map(propertySources -> ((EnumerablePropertySource<?>) propertySources).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .distinct()
                    //민간 정보 값 일부 보여주는 방식
                    .forEach(propertyName -> {
                        String rawValue = environment.getProperty(propertyName);
                        boolean sensitive = propertyName != null && sensitiveKeyPattern.matcher(propertyName).matches();
                        String displayValue = maskHalfWithAsterisks(rawValue, sensitive); // 민감값은 절반 노출 + '***' 마스킹

                        globalEnvironment.append(String.format("%s : %s%n", propertyName, displayValue));
                    });

            String logTag = Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_GlobalEnvLog_At_Main.class).get("value"), "");
            log.info(LoggingUtil.makeBaseForm(logTag, "Global Environment (Notice!! : It has Confidential Details)", globalEnvironment.toString()));
        }
    }

    // 값의 절반만 보여주고 뒤는 마스킹. null/빈 문자열/아주 짧은 값은 "***"만 노출
    private static String maskHalfWithAsterisks(String value, boolean sensitive) {
        if (!sensitive) return String.valueOf(value);
        if (value == null || value.isEmpty()) return "****..";
        int half = value.length() / 2; // 내림
        if (half <= 0) return "****..";
        return value.substring(0, half) + "****..";
    }

    //민감 키워드 설정
    private Set<String> getSensitiveKeyword() {
        return Set.of(
                "line.separator",
                "CommonProgramFiles",
                "path",
                "credentials",
                "password",
                "token",
                "secret",
                "CHARSET",
                "encoding",
                "home",
                "program",
                "java",
                "dir"
        );
    }
}
