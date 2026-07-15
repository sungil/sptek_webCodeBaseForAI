package com._sptek.__webFramework.api.response;

import com._sptek.__webFramework.observability.timing.RequestTimestampDto;
import com._sptek.__webFramework.web.util.RequestUtil;

public class ApiBaseResponseDto {
    public String resultCode;
    public String resultMessage;
    public String requestTime;
    public String responseTime;
    public String durationMsec;

    public void makeTimestamp() {
        RequestTimestampDto requestTimestampDto = RequestUtil.traceRequestDuration();
        this.requestTime = requestTimestampDto.getStartTime();
        this.responseTime = requestTimestampDto.getCurrentTime();
        this.durationMsec = requestTimestampDto.getDurationMsec();
    }
}
