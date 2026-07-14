package com._sptek._webFrameworkExample.unit.event;

import com._sptek.__webFramework.event.support.SptBaseEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class MyEvent extends SptBaseEvent {
    private String extraField;
}
