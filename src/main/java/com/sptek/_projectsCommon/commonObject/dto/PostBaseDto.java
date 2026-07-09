package com.sptek._projectsCommon.commonObject.dto;

import lombok.Data;

import java.util.List;

@Data
public abstract class PostBaseDto {
    private Long postId;
    private Integer boardId;
    private String boardName;

    private String createAt;
    private String updateAt;

    private Long userId;
    private String userEmail;
    private String userName;

    private List<UploadFileDto> uploadFileDtos;
}
