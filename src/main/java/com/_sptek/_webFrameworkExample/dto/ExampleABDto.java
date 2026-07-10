package com._sptek._webFrameworkExample.dto;

import com._sptek.__webFramework.security.crypto.Enable_DecryptAuto_At_DtoString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleABDto {
    @Enable_DecryptAuto_At_DtoString
    private String abString1;
    private String abString2;
    private ExampleADto exampleADto;
    private ExampleBDto exampleBDto;

}
