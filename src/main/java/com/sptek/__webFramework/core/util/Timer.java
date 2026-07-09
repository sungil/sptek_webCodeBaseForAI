package com.sptek.__webFramework.core.util;

import com.sptek.__webFramework.observability.processTime.Enable_ExecutionTimer_At_Main;
import com.sptek.__webFramework.core.constant.CommonConstants;
import com.sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
/**
 * 메인 클래스의 실행 시간 측정 어노테이션 설정에 따라 코드 블록 수행 시간을 로깅하는 유틸리티.
 */
public class Timer {
    private Timer() {}
    private static volatile Boolean has_Enable_ExecutionTimer_At_Main = null;

    /**
     * 매번 어노테이션 스캔을 하지 않도록 실행 시간 측정 활성화 여부를 캐싱한다.
     */
    private static boolean checkAnnotation() {
        if (has_Enable_ExecutionTimer_At_Main != null) return has_Enable_ExecutionTimer_At_Main;
        return has_Enable_ExecutionTimer_At_Main = MainClassAnnotationRegister.hasAnnotation(Enable_ExecutionTimer_At_Main.class);
    }

    /**
     * 반환값 없는 작업을 실행하고, 설정이 활성화되어 있으면 수행 시간을 로그로 남긴다.
     */
    public static void measure(String logTag, Runnable runnable) {
        if (checkAnnotation()) {
            long start = System.nanoTime();
            try {
                // 참고! 호출자 동일 쓰레드에서 동작함 new Thread(runable).start() 와 다름
                runnable.run();
            } finally {
                long end = System.nanoTime();
                log.info(CommonConstants.FW_LOG_PREFIX + "{} took {} ms", logTag, (end - start) / 1_000_000.0);
            }
        } else {
            runnable.run();
        }
    }

    /**
     * 반환값 있는 작업을 실행하고, 설정이 활성화되어 있으면 수행 시간을 로그로 남긴다.
     */
    public static <T> T measure(String logTag, Supplier<T> supplier) {
        if (checkAnnotation()) {
            long start = System.nanoTime();
            try {
                return supplier.get();
            } finally {
                long end = System.nanoTime();
                log.info(CommonConstants.FW_LOG_PREFIX + "{} took {} ms", logTag, (end - start) / 1_000_000.0);
            }
        } else {
            return supplier.get();
        }
    }

    /**
     * InterruptedException 전파 없이 sleep 하고, interrupt 상태는 복원한다.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
