package com.sptek.__webFramework.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleGoodsNProductDto {
    //동일한 클레스인 경우 매핑 가능
    private ExampleProductDto exampleProductDto;

    //클레스 형태의 경우 내부 필드를 열어서 확인하는 것이 아니기 때문에 매핑되지 않음 (내부 필드는 매핑될수 있는 구조 일지라도)
    private ExampleGoodsDto exampleGoodsDto;

    private String goodsName;
    private String productName;
}
