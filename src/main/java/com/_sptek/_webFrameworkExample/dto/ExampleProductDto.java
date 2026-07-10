package com._sptek._webFrameworkExample.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleProductDto {
    private String manufacturerName;
    private String productName;
    private long productPrice;
    private int weight;
    private int curDiscountRate;
    private int quantity;
    private boolean isAvailableReturn;


//    public long getDiscountedPrice(){
//        return getProductPrice() * (100 - getDiscountRate()) / 100;
//    }
}
