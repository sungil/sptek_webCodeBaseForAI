package com._sptek._webFrameworkExample.aiExample.event;

import com._sptek.__webFramework.event.support.SptBaseEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class AiExampleEvent extends SptBaseEvent {
    private String featureName;
    private String payload;
}
