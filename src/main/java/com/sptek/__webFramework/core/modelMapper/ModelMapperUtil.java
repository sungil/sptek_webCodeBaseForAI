package com.sptek.__webFramework.core.modelMapper;

import com.sptek.__webFramework.example.dto.ExampleADto;
import com.sptek.__webFramework.example.dto.ExampleBDto;
import com.sptek.__webFramework.example.dto.ExampleGoodsDto;
import com.sptek.__webFramework.example.dto.ExampleProductDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

/**
 * 정적 singleton ModelMapper를 사용해 DTO 간 매핑을 수행하는 유틸리티.
 *
 * <p>TypeMap cache를 유지하기 위해 내부 ModelMapper는 static 인스턴스로 관리한다. Spring Bean 기반
 * {@code ModelMapperConfig}와 별도 경로이므로 매핑 규칙 중복 여부를 함께 관리해야 한다.</p>
 */
@Slf4j
public class ModelMapperUtil {
    private static final ModelMapper defaultModelMapper = createDefaultModelMapper();

    /**
     * TypeMap cache를 유지하는 기본 ModelMapper 인스턴스를 반환한다.
     */
    private static ModelMapper getdefaultModelMapper() {
        return defaultModelMapper;
    }

    /**
     * 프레임워크 기본 매핑 전략과 예제 DTO TypeMap을 포함한 ModelMapper를 생성한다.
     */
    private static ModelMapper createDefaultModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STANDARD) //MatchingStrategies.LOOSE, MatchingStrategies.STRICT
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) //get,set이 없는 외부 클레의 private 필드에 직접 접근 가능
                .setSkipNullEnabled(true) //src 쪽 값이 null 일때 바인딩 하지 않으며 des쪽 값을 그데로 유지함
                .setAmbiguityIgnored(true); //모호한 매핑 상황 에서 에러를 ex를 발생 시키지 않고 mapper 가 판단 하여 처리함

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

    /**
     * 기본 ModelMapper로 source 객체를 destination 타입으로 변환하고 수행 시간을 debug 로그로 남긴다.
     */
    public static <S, D> D map(S sourceObject, Class<D> destinationType) {
        //for execute time test.
        long starttime = System.currentTimeMillis();

        ModelMapper modelMapper = getdefaultModelMapper();
        D result = modelMapper.map(sourceObject, destinationType);
        log.debug("Executed time : {}", (System.currentTimeMillis()-starttime));
        return result;
    }

}


