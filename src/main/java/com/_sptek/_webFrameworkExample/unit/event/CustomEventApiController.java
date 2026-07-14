package com._sptek._webFrameworkExample.unit.event;

import com._sptek.__webFramework.api.response.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com._sptek.__webFramework.api.response.Enable_ResponseOfApiGlobalException_At_RestController;
import com._sptek.__webFramework.event.support.SptEventPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Event Publish & Listener", description = "")

public class CustomEventApiController {
    private final SptEventPublisher sptEventPublisher;

    @GetMapping("/01/example/event/customEventPublish")
    @Operation(summary = "01. Custom Event Publish", description = "")
    public Object customEventPublish() throws Exception {
        sptEventPublisher.publishEvent(
                MyEvent.builder()
                        .eventMessage("this is MyEvent message")
                        .extraField("hello").build());
        return "MyEvent has been published. See the console log for details.";
    }

    // todo : 컴포넌트 클레스 내부 메소드드 어디에서도 @EventListener 를 통해 바로 listen 가능
    @EventListener
    public void listenMyEvent(MyEvent myEvent) {
        log.debug("Event! : {}", myEvent.toString());
    }
}
