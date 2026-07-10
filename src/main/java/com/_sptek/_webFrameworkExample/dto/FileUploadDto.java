package com._sptek._webFrameworkExample.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
@Slf4j
public class FileUploadDto implements Serializable {
    private String originFileName;
    private String finalFileName;

    public String getOriginFileName() {
        return URLEncoder.encode(originFileName, StandardCharsets.UTF_8);
    }
    public String getFinalFileName() {
        return URLEncoder.encode(finalFileName, StandardCharsets.UTF_8);
    }
}
