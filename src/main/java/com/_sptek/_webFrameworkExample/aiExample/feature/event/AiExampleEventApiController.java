package com._sptek._webFrameworkExample.aiExample.feature.event;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.event.core.SptEventPublisher;
import com._sptek._webFrameworkExample.aiExample.event.AiExampleEvent;
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
@RequestMapping(value = "/api/ai-example/event", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "AI Example - Event", description = "SptBaseEvent and SptEventPublisher sample")
public class AiExampleEventApiController {
    private final SptEventPublisher sptEventPublisher;

    @GetMapping("/publish")
    @Operation(summary = "Publish a sample framework event")
    public Object publish() {
        sptEventPublisher.publishEvent(AiExampleEvent.builder()
                .eventMessage("aiExample event")
                .featureName("event")
                .payload("sample payload")
                .build());
        return "published";
    }
}
