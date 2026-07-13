package com._sptek.__webFramework.core.exception;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * 비동기 실행이나 프록시 호출에서 래핑된 예외의 실제 원인 예외를 찾는 유틸리티.
 */
public class ThrowableUnwrapSupport {
    private ThrowableUnwrapSupport() {}

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
