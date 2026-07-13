package com._sptek._webFrameworkExample.aiExample.common.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiExamplePostDto {
    private Long postId;
    private Integer boardId;
    private String boardName;
    private String title;
    private String content;
    private Long userId;
    private String userEmail;
    private String userName;
    private List<AiExampleUploadedFileDto> uploadedFiles = new ArrayList<>();
}
