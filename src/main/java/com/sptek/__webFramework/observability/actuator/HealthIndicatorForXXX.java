package com.sptek.__webFramework.observability.actuator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HealthIndicatorForXXX implements HealthIndicator {
    // actuator/health 에 custom한 요소를 추가 할 수 있다.
    // 검사 항목의 기본 요소는 spring 설정 및 포함된 jar 파일들에 따라 actuator 가 정하게 된다. (추가하거나 뺄수 있음)
    // 모든 검사 항목이 Up 일때 최종 UP 됨

    @Override
    public Health health() {
        if (isHealthy()) return Health.up().withDetail("customStatus", "서비스 정상").build();
        return Health.down().withDetail("customStatus", "문제 발생!").build();
    }

    private boolean isHealthy() {
        // do more you want to check
        return true;
    }
}
