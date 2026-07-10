package com._sptek._webFrameworkExample.aiExample.feature.async;

import com._sptek.__webFramework.api.deduplicationRequest.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.web.asyncResponse.Enable_AsyncController_At_RestControllerMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = "/api/ai-example/async", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "AI Example - Async", description = "Async controller and service sample")
public class AiExampleAsyncApiController {
    private final AiExampleAsyncService aiExampleAsyncService;

    @GetMapping("/parallel")
    @Enable_AsyncController_At_RestControllerMethod
    @Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod
    @Operation(summary = "Run parallel jobs with the framework task executor")
    public Object parallel() {
        return aiExampleAsyncService.runParallelJobs();
    }
}
