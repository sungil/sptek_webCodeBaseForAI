package com._sptek.__webFramework.api.response;

import com._sptek.__webFramework.observability.timing.RequestDurationDto;
import com._sptek.__webFramework.web.util.RequestUtil;

public class ApiBaseResponseDto {
    public String resultCode;
    public String resultMessage;
    public String requestTime;
    public String responseTime;
    public String durationMsec;

    public void makeTimestamp() {
        RequestDurationDto requestDurationDto = RequestUtil.traceRequestDuration();
        this.requestTime = requestDurationDto.getStartTime();
        this.responseTime = requestDurationDto.getCurrentTime();
        this.durationMsec = requestDurationDto.getDurationMsec();
    }
}
