package com._sptek.__webFramework.web.error;

import com._sptek.__webFramework.observability.logging.LoggingConstants;
import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import com._sptek.__webFramework.api.response.ApiCommonErrorResponseDto;
import com._sptek.__webFramework.core.resultCode.CommonErrorCodeEnum;
import com._sptek.__webFramework.core.util.ExceptionUtil;
import com._sptek.__webFramework.observability.logging.LoggingUtil;
import com._sptek.__webFramework.web.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

//@Profile(value = { "notused" })
//@Conditional(ApplicationGlobalExceptionHandler.ApplicationGlobalExceptionHandlerCondition.class) //@HasAnnotationOnMainForBean 방식 으로 변경함

@Slf4j
@HasAnnotationOnMain_At_Bean(Enable_ResponseOfApplicationGlobalException_At_Main.class)
// todo: (중요) CustomErrorController 전용 처리 (API/VEW 모두 가능)
@ControllerAdvice(assignableTypes = {CustomErrorController.class})

public class ApplicationGlobalExceptionHandler {
    // 이 핸들러 는 CustomErrorController 를 통해 인입된 상위 레벨 에러 처리 만을 하는게 목적 이다.
    // 상위 레벨이 아닌 Controller 내부 진입 후 에러에 대해 서는 ViewGlobalExceptionHandler 와 ApiGlobalExceptionHandler 에서 처리 한다.

    // 401 (실제로 spring에서 401은 발생이 안된고 403으로 발생됨)
    @ExceptionHandler({AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Object handleAuthenticationException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // todo : view 요청에서 로그인이 안된상태여서 권한 에러가 났을때는 에러 페이지 보단 로그인 페이지로 더 친절히 이동해 줄까?
        return handleError(request, response, ex, CommonErrorCodeEnum.FORBIDDEN_ERROR, "error/commonAuthenticationError");
    }

    // 403
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    //controller 에서 hasRole 이든 hasAuthority 든 AccessDeniedException 이 발생됨 (hasRole인 경우는 401 같지는 403이 나옴)
    public Object handleAccessDeniedException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // todo : view 요청에서 로그인이 안된상태여서 권한 에러가 났을때는 에러 페이지 보단 로그인 페이지로 더 친절히 이동해 줄까?
        return handleError(request, response, ex, CommonErrorCodeEnum.FORBIDDEN_ERROR, "error/commonAuthenticationError");
    }

    // 404
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    //요청에 대한 url 매핑 자체가 없기 때문에 ApplicationGlobalExceptionHandler 로 들어옴
    public Object handleNoResourceFoundException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return handleError(request, response, ex, CommonErrorCodeEnum.NOT_FOUND_ERROR, "error/commonNotfoundError");
    }

    // 405
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    // 지원하지 않는 request Metho(GET, POST, PUT, DELETE...)로 요청 했을때
    public Object handleHttpRequestMethodNotSupportedException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return handleError(request, response, ex, CommonErrorCodeEnum.METHOD_NOT_ALLOWED, "error/commonMethodNotSupportError");
    }

    // todo: 413, 실제로 413은 이곳 으로 도달 하지 못함(원인 확인 필요)
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public Object handleMaxUploadSizeExceededException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return handleError(request, response, ex, CommonErrorCodeEnum.PAYLOAD_EXCEEDED_ERROR, "error/commonInternalError");
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object responseStatusException(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return handleError(request, response, ex, CommonErrorCodeEnum.INTERNAL_SERVER_ERROR, "error/commonInternalError");
    }

    //view 와 api 요청을 구분 하여 최종 처리 함 (이곳에서 처리되는 경우는 ReqResDeailLogFilter 로 진입이 불가능한 케이스임으로 여기로 로그를 처리함)
    private Object handleError(HttpServletRequest request, HttpServletResponse response, Exception ex, CommonErrorCodeEnum commonErrorCodeEnum, String viewName) throws Exception {
        LoggingUtil.exLoggingAndReturnThrowable(log, ex);

        if (RequestUtil.isApiRequest(request)) {
            ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(commonErrorCodeEnum, ExceptionUtil.getRealException(ex).getMessage());
            LoggingUtil.reqResDetailLogging(log, request, response, apiCommonErrorResponseDto, "Req Res Detail Log From " + this.getClass().getSimpleName());
            return new ResponseEntity<>(apiCommonErrorResponseDto, commonErrorCodeEnum.getHttpStatusCode());
        } else {
            //view 요청에서 발생한 에러의 경우 이후에 구체적으로 어떤 에러가 발생했는지 정확히 알수 없기 때문에 저장해서 사용함.
            request.setAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE, ExceptionUtil.getRealException(ex).getMessage());
            LoggingUtil.reqResDetailLogging(log, request, response, "Req Res Detail Log From " + this.getClass().getSimpleName());
            return viewName;
            //return "error/XXX" // spring 호출 페이지와 통일할 수 도 있음
        }
    }
}
