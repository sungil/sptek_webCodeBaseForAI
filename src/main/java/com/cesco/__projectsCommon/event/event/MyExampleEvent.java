package com.cesco.__projectsCommon.event.event;

import com._sptek.__webFramework.event.core.SptBaseEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class MyExampleEvent extends SptBaseEvent {
    private String extraField;
}
