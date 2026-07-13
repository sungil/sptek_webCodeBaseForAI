package com._sptek._webFrameworkExample.aiExample.feature.error;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek._webFrameworkExample.aiExample.common.code.AiExampleServiceErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = "/api/ai-example/error", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "AI Example - Error", description = "ServiceException sample")
public class AiExampleErrorApiController {
    private final AiExampleErrorService aiExampleErrorService;

    @GetMapping("/service-error")
    @Operation(summary = "Throw a ServiceException with a sample error code")
    public Object serviceError() {
        throw new ServiceException(AiExampleServiceErrorCode.NO_SAMPLE_RESOURCE);
    }

    @GetMapping("/available-id")
    @Operation(summary = "Use ServiceException at the exact business rule failure point")
    public Object availableId(@RequestParam String sampleId) {
        return aiExampleErrorService.isAvailableSampleId(sampleId);
    }
}
