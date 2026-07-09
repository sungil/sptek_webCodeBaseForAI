package com.sptek.__webFramework.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleGoodsDto {
    private String manufacturerName;
    private String name;
    private long originPrice ;
    private int weight;
    private long discountedPrice;
    private int stock;
    private String availableSendBackYn;
}
