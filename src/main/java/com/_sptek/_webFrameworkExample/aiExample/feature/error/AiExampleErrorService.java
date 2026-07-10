package com._sptek._webFrameworkExample.aiExample.feature.error;

import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek._webFrameworkExample.aiExample.common.code.AiExampleServiceErrorCode;
import org.springframework.stereotype.Service;

@Service
public class AiExampleErrorService {

    public boolean isAvailableSampleId(String sampleId) {
        boolean alreadyExists = "already-exists".equalsIgnoreCase(sampleId);
        if (alreadyExists) {
            throw new ServiceException(
                    AiExampleServiceErrorCode.DUPLICATED_SAMPLE_RESOURCE,
                    sampleId + " is already used."
            );
        }
        return true;
    }
}
