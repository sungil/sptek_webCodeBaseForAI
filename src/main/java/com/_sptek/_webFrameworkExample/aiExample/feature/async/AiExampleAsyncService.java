package com._sptek._webFrameworkExample.aiExample.feature.async;

import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek.__webFramework.core.util.Timer;
import com._sptek._webFrameworkExample.aiExample.common.code.AiExampleServiceErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AiExampleAsyncService {
    private final TaskExecutor taskExecutor;

    public AiExampleAsyncService(@Qualifier("sptTaskExecutor") TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public List<AiExampleAsyncResult> runParallelJobs() {
        CompletableFuture<AiExampleAsyncResult> firstJob = CompletableFuture.supplyAsync(
                () -> runJob("firstJob", 100L),
                taskExecutor
        );
        CompletableFuture<AiExampleAsyncResult> secondJob = CompletableFuture.supplyAsync(
                () -> runJob("secondJob", 100L),
                taskExecutor
        );

        return List.of(firstJob.join(), secondJob.join());
    }

    public AiExampleAsyncResult runJob(String jobName, long delayMillis) {
        Timer.sleep(delayMillis);
        if (jobName == null || jobName.isBlank()) {
            throw new ServiceException(AiExampleServiceErrorCode.DEFAULT_ERROR, "jobName is required.");
        }
        return new AiExampleAsyncResult(jobName, "success");
    }

    public record AiExampleAsyncResult(String jobName, String result) {
    }
}
