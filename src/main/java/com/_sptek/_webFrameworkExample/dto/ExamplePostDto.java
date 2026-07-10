package com._sptek._webFrameworkExample.dto;

import com.cesco.__projectsCommon.commonObject.dto.PostBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExamplePostDto extends PostBaseDto {
    String title;
    String content;
}
