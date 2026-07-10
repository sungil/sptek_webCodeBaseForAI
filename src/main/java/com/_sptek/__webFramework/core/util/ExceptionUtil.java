package com._sptek.__webFramework.core.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Slf4j
/**
 * 예외를 기본값으로 흡수하거나 비동기/프록시 래핑 예외의 실제 원인을 찾는 유틸리티.
 */
public class ExceptionUtil {
    /**
     * 예외를 던질 수 있는 Supplier 형태를 공통 안전 실행 API에서 사용하기 위한 함수형 인터페이스.
     */
    @FunctionalInterface
    public interface SupplierWithEx<T> {
        T get();// throws Exception;
    }

    /**
     * 예외를 던질 수 있는 Runnable 형태를 공통 안전 실행 API에서 사용하기 위한 함수형 인터페이스.
     */
    @FunctionalInterface
    public interface RunnableWithEx {
        void run();// throws Exception;
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

    /**
     * CompletionException, ExecutionException, UndeclaredThrowableException 안쪽의 실제 예외를 찾아 반환한다.
     */
    public static Throwable getRealException(Throwable t) {
        if (t == null) return null;

        // 아래 EX 는 래핑된 경우가 많음
        while (t instanceof CompletionException || t instanceof ExecutionException || t instanceof UndeclaredThrowableException) {
            Throwable cause = t.getCause();
            if (cause == null || cause == t) {
                return t;
            }
            t = cause;
        }
        return t;
    }
}
