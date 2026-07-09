package com.sptek.__webFramework.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleBDto {
    private String bObjectFamilyTitle;
    private String bObjectEndTitle;
}
