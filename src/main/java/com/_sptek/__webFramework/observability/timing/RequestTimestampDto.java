package com._sptek.__webFramework.observability.timing;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 하나의 HTTP 요청에 대한 시작 시각, 현재 응답 시각, 경과 시간을 담는 DTO.
 */
@Data
@AllArgsConstructor
public class RequestTimestampDto {
    private String startTime;
    private String currentTime;
    private String durationMsec;
}
