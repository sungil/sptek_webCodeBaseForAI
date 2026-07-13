package com._sptek.__webFramework.core.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 모니터링이나 부가 로그 수집 중 발생한 예외를 기본값으로 흡수하는 안전 실행 유틸리티.
 */
@Slf4j
public class ExceptionSafeSupport {
    private ExceptionSafeSupport() {}

    /**
     * 예외를 던질 수 있는 Supplier 형태를 공통 안전 실행 API에서 사용하기 위한 함수형 인터페이스.
     */
    @FunctionalInterface
    public interface SupplierWithEx<T> {
        T get();
    }

    /**
     * 예외를 던질 수 있는 Runnable 형태를 공통 안전 실행 API에서 사용하기 위한 함수형 인터페이스.
     */
    @FunctionalInterface
    public interface RunnableWithEx {
        void run();
    }

    /**
     * 실행 중 예외가 발생하면 예외를 전파하지 않고 지정 기본값을 반환한다.
     */
    public static <T> T exSafe(SupplierWithEx<T> s, T defaultValue) {
        try {
            return s.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 반환값 없는 작업 실행 중 예외가 발생하면 로그만 남기고 전파하지 않는다.
     */
    public static void exSafe(RunnableWithEx r) {
        try {
            r.run();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
