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
public class ExampleADto {
    @Enable_DecryptAuto_At_DtoString
    private String aDtoFirstName;
    @Enable_DecryptAuto_At_DtoString
    private String aDtoLastName;
}
