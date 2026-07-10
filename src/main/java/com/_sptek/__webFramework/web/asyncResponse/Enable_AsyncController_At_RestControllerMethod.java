package com._sptek.__webFramework.web.asyncResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/**
 * RestController 메서드에 붙여 해당 API 응답을 프레임워크 비동기 컨트롤러 흐름으로 처리하도록 표시하는 애노테이션.
 *
 * <p>{@code AsyncControllerReturnValueHandler}가 메서드 반환값을 비동기 처리 대상으로 인식하고,
 * {@code ApiCommonResponseHelperAspect}는 이 표시가 있는 요청의 공통 응답 후처리 방식을 분기한다.</p>
 */
public @interface Enable_AsyncController_At_RestControllerMethod {
}
