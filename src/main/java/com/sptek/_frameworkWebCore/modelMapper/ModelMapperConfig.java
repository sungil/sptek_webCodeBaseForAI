package com.sptek._frameworkWebCore.modelMapper;

import com.sptek._frameworkWebCore._example.dto.ExampleADto;
import com.sptek._frameworkWebCore._example.dto.ExampleBDto;
import com.sptek._frameworkWebCore._example.dto.ExampleGoodsDto;
import com.sptek._frameworkWebCore._example.dto.ExampleProductDto;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DTO 간 필드 복사를 위한 전역 ModelMapper Bean 설정.
 *
 * <p>기본 매칭 전략과 null skip 정책을 지정하고, 예제 DTO 간 이름이 다른 필드 매핑 사례를 함께 등록한다.
 * 업무 도메인의 복잡한 매핑은 공통 Bean을 오염시키지 않도록 별도 TypeMap 추가 위치를 검토한다.</p>
 */
@Configuration
public class ModelMapperConfig {
    /**
     * 프레임워크 기본 ModelMapper 설정과 예제 TypeMap을 구성한다.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) //MatchingStrategies.LOOSE, MatchingStrategies.STRICT
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) //get,set이 없는 외부 클레의 private 필드에 직접 접근가능
                .setSkipNullEnabled(true) //src쪽 값이 null 일때 바인딩하지 않으며 des쪽 값을 그데로 유지함
                .setAmbiguityIgnored(true); //모호한 매핑상황에서 에러를 ex를 발생시키지 않고 mapper가 판단하여 처리함

        //todo: 계속해서 추가? (괜찮은 방법일까? 고민필요)
        modelMapper.createTypeMap(ExampleProductDto.class, ExampleGoodsDto.class).addMappings(
                mapper -> {
                    mapper.map(ExampleProductDto::getProductName, ExampleGoodsDto::setName);
                    mapper.map(ExampleProductDto::getProductPrice, ExampleGoodsDto::setOriginPrice);
                    mapper.map(ExampleProductDto::getQuantity, ExampleGoodsDto::setStock);
                    mapper.using((Converter<Boolean, String>) context -> context.getSource() ? "Y" : "N")
                            .map(ExampleProductDto::isAvailableReturn, ExampleGoodsDto::setAvailableSendBackYn);
                });

        modelMapper.createTypeMap(ExampleADto.class, ExampleBDto.class).addMappings(
                mapper -> {
                    mapper.map(ExampleADto::getADtoLastName, ExampleBDto::setBObjectEndTitle);
                    mapper.map(ExampleADto::getADtoFirstName, ExampleBDto::setBObjectFamilyTitle);
                });

        return modelMapper;
    }
}
