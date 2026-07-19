package com._sptek.__webFramework.security.crypto.config;

import com._sptek.__webFramework.security.crypto.annotation.Enable_EncryptorJasypt_At_Main;
import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com._sptek.__webFramework.security.crypto.registry.EncryptorRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * 메인 클래스의 Jasypt 활성화 애노테이션이 있을 때 문자열 암호화 Bean을 구성한다.
 *
 * <p>정적 설정값이나 property 주요 정보를 암복호화하는 용도의 PBE 기반 모듈이다.
 * 실시간 대량 데이터 암복호화보다는 설정값 보호에 맞춰 사용하고, 생성한 Bean은
 * {@link EncryptorRegistry}에 {@code sptJASYPT} 타입으로 등록한다.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
@HasAnnotationOnMain_At_Bean(Enable_EncryptorJasypt_At_Main.class)
public class JasyptEncryptorConfig {

    final private Environment environment;

    //@Primary
    /**
     * Jasypt PBE 문자열 암호화 Bean을 만들고 전역 암호화 레지스트리에 등록한다.
     *
     * <p>운영 환경에서는 {@code jasypt.encryptor.password}를 환경 변수 등 외부 비밀값으로 주입해야 하며,
     * password 값은 로그에 남기지 않는다.</p>
     */
    @Bean(name = "customJasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        String pbePassword = environment.getProperty("jasypt.encryptor.password");
        String pbeAlgorithm = environment.getProperty("jasypt.encryptor.algorithm", "PBEWITHHMACSHA512ANDAES_256");
        //log.debug("pbePassword({}), pbeAlgorithm({})", StringUtils.hasText(pbePassword) ? pbePassword.substring(0, pbePassword.length()/2)+"..." : "", pbeAlgorithm);

        if(!StringUtils.hasText(pbePassword)) {
            log.error(">>#### Secure Notice : JASYPT_ENCRYPTOR 의 PBE_PASSWORD 설정이 필요 합니다.");
            throw new IllegalStateException(String.format("Required configuration value is missing: PBE_PASSWORD = %s", pbePassword));
        }

        PooledPBEStringEncryptor pooledPBEStringEncryptor = new PooledPBEStringEncryptor();
        pooledPBEStringEncryptor.setConfig(getSimpleStringPBEConfig(pbePassword, pbeAlgorithm));

        //Encryption 에 사용 등록 처리
        EncryptorRegistry.register(EncryptorRegistry.Type.sptJASYPT, pooledPBEStringEncryptor);

        //최종 인코딩된 암호값에는 알고리즘정보 및 salt가 포함됨(salt가 포함됨으로 별도로 salt를 관리할 필요가 없음, salt가 노출된다고 해도 암호를 풀 방법이 쉬워질건 없음)
        return pooledPBEStringEncryptor;
    }

    /**
     * Jasypt PBE 구현체에 전달할 기본 암호화 설정을 구성한다.
     *
     * <p>프로젝트 보안 기준이 더 높은 환경에서는 반복 횟수, pool 크기, 알고리즘을 환경별로 재검토한다.</p>
     */
    private static @NotNull SimpleStringPBEConfig getSimpleStringPBEConfig(String pbePassword, String pbeAlgorithm) {
        SimpleStringPBEConfig simpleStringPBEConfig = new SimpleStringPBEConfig();
        simpleStringPBEConfig.setPassword(pbePassword); // 암호화에 사용할 대칭키
        simpleStringPBEConfig.setAlgorithm(pbeAlgorithm); //사용 알고리즘

        simpleStringPBEConfig.setKeyObtentionIterations("10000"); // 복호화 어렵게 하기 위해 해싱을 몇번 돌릴지의 설정 5000 이상 권장(늘릴수록 시간이 늘어남으로 적절히 조절)
        simpleStringPBEConfig.setPoolSize("1"); // 해당 모듈의 pool로 디볼트1, 멀티스레드 환경에서는 늘릴수 있음
        simpleStringPBEConfig.setProviderName("SunJCE");
        simpleStringPBEConfig.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        simpleStringPBEConfig.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        simpleStringPBEConfig.setStringOutputType("base64"); // 최종 결과값 출력 인코딩

        return simpleStringPBEConfig;
    }


}
