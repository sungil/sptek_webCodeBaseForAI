package com.sptek.__webFramework.observability.processTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExcuteTimeDto {
    private String startTime;
    private String currentTime;
    private String durationMsec;
}
