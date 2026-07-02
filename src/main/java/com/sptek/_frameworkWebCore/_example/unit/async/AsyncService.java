package com.sptek._frameworkWebCore._example.unit.async;

import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.util.AuthenticationUtil;
import com.sptek._frameworkWebCore.util.LocaleUtil;
import com.sptek._frameworkWebCore.util.Timer;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 긴 작업과 병렬 작업을 서비스 계층에서 비동기로 구성하는 방식을 비교하는 예제 서비스.
 *
 * <p>요청 thread와 분리된 작업에서도 MDC, request, locale, dateTime 같은 ThreadLocal 기반 컨텍스트가 필요하면
 * 프레임워크의 {@code sptTaskExecutor}를 사용한다. 단순히 {@code @Async}를 붙이는 방식은 self-invocation,
 * 반환 타입 변경, 예외 전파 문제가 생기기 쉬우므로, 결과 조합이 필요한 업무에서는 {@code CompletableFuture}와
 * 명시적인 executor를 사용해 작업을 나누고 {@code join()} 또는 {@code get()}으로 예외와 결과를 회수하는 구성을 우선 참고한다.</p>
 */
@Slf4j
@Service
public class AsyncService {

    private final TaskExecutor taskExecutor;

    public AsyncService(@Qualifier("sptTaskExecutor") TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * 반환값이 없는 긴 작업을 동기 방식으로 실행하는 기준 예시.
     *
     * <p>이 메서드를 executor로 감싸 호출하면 하위 thread에서 프레임워크 컨텍스트가 이관되는지도 함께 확인할 수 있다.</p>
     */
    public void noReturnJob() {
        Timer.sleep(10_000L);
        if (false) throw new RuntimeException("returnObjectJob RuntimeException");
        log.debug("noReturnJob done");

        String userName = AuthenticationUtil.getMyName();
        String userLanguageTag = LocaleUtil.getCurUserLanguageTag();
        String userTimeZone = LocaleUtil.getCurUserTimeZoneName();
        log.debug("userName: {}, userLanguageTag: {}, userTimeZone: {}", userName, userLanguageTag, userTimeZone);
    }

    /**
     * 반환값이 있는 긴 작업을 동기 방식으로 실행하는 기준 예시.
     *
     * <p>{@code @Async}를 직접 붙이려면 Future 계열 반환 타입으로 계약이 바뀌므로,
     * 이 형태는 {@code CompletableFuture.supplyAsync(..., sptTaskExecutor)}로 감싸 병렬 작업에 재사용한다.</p>
     */
    public AsyncDto returnObjectJob() {
        Timer.sleep(10_000L);
        if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "처리할 작업이 없습니다");
        return new AsyncDto("returnObjectJob", "success");
    }

    /**
     * 반환값이 없는 작업에 {@code @Async}를 직접 적용한 비교 예시.
     *
     * <p>같은 클래스 내부 호출에는 Spring proxy가 적용되지 않는 self-invocation 제약과 예외 전파 복잡성이 있으므로,
     * 실제 업무에서는 호출 경로와 예외 처리 방식을 먼저 확인한다.</p>
     */
    @Async("sptTaskExecutor")
    public void noReturnJobWithAsync() {
        Timer.sleep(10_000L);
        if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "noReturnJobWithAsync ServiceException");
        log.debug("noReturnJobWithAsync done");
    }

    /**
     * 반환값이 있는 작업에 {@code @Async}를 직접 적용해 {@code CompletableFuture}로 반환하는 비교 예시.
     *
     * <p>서비스 API의 반환 타입이 비동기 계약으로 바뀌고 예외 회수 위치가 달라지므로,
     * 컨트롤러 응답 비동기화와 조합할 때는 공통 응답 AOP와 return value handler의 처리 방식을 함께 확인한다.</p>
     */
    @Async("sptTaskExecutor")
    public CompletableFuture<AsyncDto> returnObjectJobWithAsync() {
        Timer.sleep(10_000L);
        if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "처리할 작업이 없습니다.");
        return CompletableFuture.completedFuture(new AsyncDto("returnObjectJobWithAsync", "success"));
    }

    /**
     * 결과가 필요한 여러 작업을 {@code CompletableFuture}로 병렬 실행하고 결과를 조합하는 권장 예시.
     *
     * <p>동기 메서드를 executor로 감싸 재사용하면 반환 타입을 업무 메서드 기준으로 유지하면서도 병렬 처리가 가능하다.
     * 비동기 작업의 예외를 공통 예외 흐름으로 넘기려면 반환값이 없는 작업도 {@code join()} 또는 {@code get()}으로 회수한다.</p>
     */
    public List<AsyncDto> recommendAsyncJoin() throws Exception {
        var noReturnJob = CompletableFuture.runAsync(this::noReturnJob, taskExecutor);
        var returnObjectJob = CompletableFuture.supplyAsync(this::returnObjectJob, taskExecutor);
        var composeJob = CompletableFuture.supplyAsync(() -> {
            Timer.sleep(10_000L);
            if (false) throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "처리할 작업이 없습니다.");
            return new AsyncDto("composeJob", "success");}, taskExecutor).whenComplete((r, e) -> {/*후처리*/});

        noReturnJob.join();

        var asyncDtos = new ArrayList<AsyncDto>();
        asyncDtos.add(returnObjectJob.get());
        asyncDtos.add(composeJob.get());
        return asyncDtos;
    }

    /**
     * async 예제 응답에서 어떤 작업이 값을 반환했는지 보여주기 위한 테스트용 DTO.
     */
    public record AsyncDto (String whoReturn, String returnValue) {}
}
