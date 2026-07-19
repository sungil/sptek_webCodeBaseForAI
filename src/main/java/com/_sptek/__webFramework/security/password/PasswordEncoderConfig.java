package com._sptek.__webFramework.security.password;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 비밀번호 해시와 검증에 사용할 password encoder Bean 설정.
 *
 * <p>BCrypt 결과에는 salt와 strength 정보가 함께 포함되므로 별도 salt 저장소를 두지 않는다.
 * 프레임워크와 업무 코드는 구체 BCrypt 구현체가 아니라 {@link PasswordEncoder} 계약에 의존한다.</p>
 */
@Slf4j
@Configuration
public class PasswordEncoderConfig {
    /**
     * 기본 strength 10의 BCrypt 구현체를 PasswordEncoder 계약으로 등록한다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // springframework 에서 제공 하는 BCrypt 알고리즘을 사용하는 비밀번호 해시용 클레스
        // spring-security 등 에서 비밀번호 해싱 매칭용으로 사용
        // 랜덤 salt 값을 사용하며 salt값이 인코딩 결과값에 포함됨으로 따로 저장할 필요가 없음
        // 해싱 결과값은 (알고리즘버전 + 강도 + 사용된랜덤salt값 + 해싱된원문최종결과) 로 구성됨

        // 보안 강도 default = 10
        return new BCryptPasswordEncoder(10);
    }
}
