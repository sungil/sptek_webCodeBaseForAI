package com._sptek.__webFramework.view.error;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;


//@Profile(value = { "notused" })
@Slf4j
// todo: (중요) Enable_ResponseOfViewGlobalException_At_ViewController 가 적용된 클레스만 처리 (View)
@ControllerAdvice(annotations = Enable_ResponseOfViewGlobalException_At_ViewController.class)
@ConditionalOnProperty(name = "server.error.ignoreCustomErrorViewForDebug", havingValue = "false", matchIfMissing = false)

public class ViewGlobalExceptionHandler {
    // todo: viewController에서 발생되는 에러의 경우 사용자에게 공통된 에러 페이지를 보여주는것 외에 딱히 다른 처리가 있을수 있을까? 그래서 현재는 httpsttus 코드도 상세히 분리하고 있지않음, 고민필요.

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) //api 쪽의 ServiceException 경우 상황에 맞게 HttpStatus 를 내리나.. view 에서는 큰 의미가 없어 하나도 통일
    //개발자 가 의도적 으로 생성한 Exception 는 ServiceException 로 생성 하며 해당 핸들러 에서 처리 됨
    public Object handleServiceException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        return handleError(request, ex, "error/commonServiceError");
    }

    @ExceptionHandler({AuthenticationException.class, AccessDeniedException.class, HttpClientErrorException.Unauthorized.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    //기타 모든 에러를 하나로 처리함 (view 에러 에서는 특별히 공통 에러 페이지 외 구분할 필요가 없기 때문에 한번에 처리함, 에러 종류별 구분된 에러 페이지가 필요하면 추가해 나갈 것)
    public Object handleAuthenticationException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        return handleError(request, ex, "error/commonAuthenticationError");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //기타 모든 에러를 하나로 처리함 (view 에러 에서는 특별히 공통 에러 페이지 외 구분할 필요가 없기 때문에 한번에 처리함, 에러 종류별 구분된 에러 페이지가 필요하면 추가해 나갈 것)
    public Object handleUnexpectedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        return handleError(request, ex, "error/commonInternalError");
    }

    private Object handleError(HttpServletRequest request, Exception ex, String viewName) {
        Throwable t = LoggingUtil.exLoggingAndReturnThrowable(log, ex);
        //view 요청 에서 발생한 에러의 경우 이후에 구체적 으로 어떤 에러가 발생 했는지 정확히 알수 없기 때문에 저장 해서 사용함.
        request.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE, t.getMessage());
        return viewName;
        //return "error/XXX" // spring 호출 페이지와 통일할 수 도 있음
    }
}
