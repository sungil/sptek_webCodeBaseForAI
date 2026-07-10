package com._sptek.__webFramework.api.response;

import com._sptek.__webFramework.observability.processTime.ExcuteTimeDto;
import com._sptek.__webFramework.web.util.RequestUtil;

public class ApiBaseResponseDto {
    public String resultCode;
    public String resultMessage;
    public String requestTime;
    public String responseTime;
    public String durationMsec;

    public void makeTimestamp() {
        ExcuteTimeDto excuteTimeDto = RequestUtil.traceRequestDuration();
        this.requestTime = excuteTimeDto.getStartTime();
        this.responseTime = excuteTimeDto.getCurrentTime();
        this.durationMsec = excuteTimeDto.getDurationMsec();
    }
}
