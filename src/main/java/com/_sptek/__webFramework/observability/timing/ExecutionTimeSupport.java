package com._sptek.__webFramework.observability.timing;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
/**
 * 개발자가 명시적으로 감싼 코드 블록의 수행 시간을 로깅하는 유틸리티.
 *
 * <p>요청 전체 duration은 {@link MakeRequestTimestampFilter}와 {@code RequestUtil.traceRequestDuration()}이 담당하고,
 * 이 클래스는 메인 애노테이션 활성 여부와 무관하게 호출된 코드 블록의 elapsed time을 측정한다.</p>
 */
public class ExecutionTimeSupport {
    private ExecutionTimeSupport() {}

    /**
     * 반환값 없는 작업을 실행하고 수행 시간을 로그로 남긴다.
     *
     * <pre>{@code
     * ExecutionTimeSupport.measure("sampleTask", () -> {
     *     service.process();
     * });
     * }</pre>
     */
    public static void measure(String logTag, Runnable runnable) {
        long start = System.nanoTime();
        try {
            // 참고! 호출자 동일 쓰레드에서 동작함 new Thread(runable).start() 와 다름
            runnable.run();
        } finally {
            long end = System.nanoTime();
            log.info(LoggingConstants.FW_LOG_PREFIX + "{} took {} ms", logTag, (end - start) / 1_000_000.0);
        }
    }

    /**
     * 반환값 있는 작업을 실행하고 수행 시간을 로그로 남긴다.
     *
     * <pre>{@code
     * String result = ExecutionTimeSupport.measure("sampleQuery", () -> service.findName(id));
     * }</pre>
     */
    public static <T> T measure(String logTag, Supplier<T> supplier) {
        long start = System.nanoTime();
        try {
            return supplier.get();
        } finally {
            long end = System.nanoTime();
            log.info(LoggingConstants.FW_LOG_PREFIX + "{} took {} ms", logTag, (end - start) / 1_000_000.0);
        }
    }

    /**
     * InterruptedException 전파 없이 sleep 하고, interrupt 상태는 복원한다.
     *
     * <p>예제/테스트용 지연이며 운영 흐름 제어용으로 확장하지 말라.</p>
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
