package com._sptek._webFrameworkExample.aiExample.common.code;

import com._sptek.__webFramework.core.resultCode.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiExampleServiceErrorCode implements BaseCode {
    NO_SAMPLE_RESOURCE(HttpStatus.BAD_REQUEST, "AIE404", "The requested sample resource does not exist."),
    DUPLICATED_SAMPLE_RESOURCE(HttpStatus.CONFLICT, "AIE409", "The sample resource already exists."),
    FILE_UPLOAD_DENIED(HttpStatus.BAD_REQUEST, "AIE101", "The sample file upload request is not allowed."),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "AIE413", "The sample file upload size is too large."),
    DEFAULT_ERROR(HttpStatus.BAD_REQUEST, "AIE999", "The sample request could not be processed.");

    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;
}
