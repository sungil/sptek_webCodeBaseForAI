package com._sptek._webFrameworkExample.aiExample.common.dto;

import lombok.Data;

@Data
public class AiExampleUploadedFileDto {
    private Long postId;
    private Integer boardId;
    private String fileName;
    private Integer fileOrder;
    private String filePath;
}
