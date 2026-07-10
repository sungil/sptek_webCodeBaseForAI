package com._sptek.__webFramework.core.modelMapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DTO 간 필드 복사를 위한 전역 ModelMapper Bean 설정.
 *
 * <p>기본 매칭 전략과 null skip 정책만 지정한다.
 * 업무 도메인의 복잡한 TypeMap은 프레임워크 Bean을 오염시키지 않도록 각 프로젝트 또는 예제 서버 설정에서 추가한다.</p>
 */
@Configuration
public class ModelMapperConfig {
    /**
     * 프레임워크 기본 ModelMapper 설정을 구성한다.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) //MatchingStrategies.LOOSE, MatchingStrategies.STRICT
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) //get,set이 없는 외부 클레의 private 필드에 직접 접근가능
                .setSkipNullEnabled(true) //src쪽 값이 null 일때 바인딩하지 않으며 des쪽 값을 그데로 유지함
                .setAmbiguityIgnored(true); //모호한 매핑상황에서 에러를 ex를 발생시키지 않고 mapper가 판단하여 처리함
        return modelMapper;
    }
}
