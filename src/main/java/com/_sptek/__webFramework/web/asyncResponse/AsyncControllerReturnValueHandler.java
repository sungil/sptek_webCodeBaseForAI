package com._sptek.__webFramework.web.asyncResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
public class AsyncControllerReturnValueHandler implements HandlerMethodReturnValueHandler {

    // 핸들러 적용 조건
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        var method = returnType.getMethod();
        return method != null
                && method.isAnnotationPresent(Enable_AsyncController_At_RestControllerMethod.class)
                && returnType.getParameterType() == Object.class;
    }

    @Override
    public void handleReturnValue(Object returnInstance, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        // return Instance 의 타입을 보고 컨트롤러 이후 프로세싱을 타입에 따라 강제?로 태운다. (spring 본연의 플로우는 컨트럴로 리턴 시그니쳐를 보고 결정됨)

        // AsyncResponse 적용 케이스
        if (returnInstance instanceof CompletableFuture<?> completableFuture) {
            DeferredResult<Object> deferredResult = new DeferredResult<>();
            completableFuture.whenComplete((result, ex) -> {
                if (ex != null) {
                    Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;
                    deferredResult.setErrorResult(cause);
                } else {
                    deferredResult.setResult(result);
                }
            });
            modelAndViewContainer.setRequestHandled(false);
            WebAsyncUtils.getAsyncManager(nativeWebRequest).startDeferredResultProcessing(deferredResult, modelAndViewContainer);
            return;
        }

        // --- 추가적으로 고려 하고 있는 것 들 (해당 케이스를 현재는 구현하지 않았음)
        if (returnInstance instanceof Callable<?> callable) {
            modelAndViewContainer.setRequestHandled(false);
            WebAsyncUtils.getAsyncManager(nativeWebRequest).startCallableProcessing(new WebAsyncTask<>(callable), modelAndViewContainer);
            return;
        }
        if (returnInstance instanceof DeferredResult<?> deferredResult) {
            modelAndViewContainer.setRequestHandled(false);
            WebAsyncUtils.getAsyncManager(nativeWebRequest).startDeferredResultProcessing(deferredResult, modelAndViewContainer);
            return;
        }

        // todo: support 조건에는 맞지만 리턴 object 의 instance 조건이 맞지 않는 경우 기존 다른 체인으로 넘길까? 에러로 처리할까??
        // modelAndViewContainer.setRequestHandled(false); // 다른 기본 체인에 맡김
        throw new IllegalStateException("return Object instance is not match : " + (returnInstance == null ? "null" : returnInstance.getClass().getName())
        );
    }
}
