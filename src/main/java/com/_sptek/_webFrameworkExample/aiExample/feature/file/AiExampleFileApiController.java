package com._sptek._webFrameworkExample.aiExample.feature.file;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = "/api/ai-example/file", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "AI Example - File", description = "Multipart file validation sample")
public class AiExampleFileApiController {
    private final AiExampleFileService aiExampleFileService;

    @PostMapping(value = "/describe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Validate uploaded images and return file metadata without persistence")
    public Object describe(@RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return aiExampleFileService.validateAndDescribe(files);
    }
}
