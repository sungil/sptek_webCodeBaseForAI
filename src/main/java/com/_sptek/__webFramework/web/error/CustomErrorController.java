package com._sptek.__webFramework.web.error;

import com._sptek.__webFramework.bootstrap.annotationCondition.HasAnnotationOnMain_At_Bean;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;


@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_At_Bean(Enable_ResponseOfApplicationGlobalException_At_Main.class)
@ConditionalOnProperty(name = "web-framework.error.application-global.enabled", havingValue = "true", matchIfMissing = false)
@Controller

public class CustomErrorController implements ErrorController {
    //Controller 외부 영역 에서 발생한 에러(필터 쪽이나.. 기타 등등)를 직접 처리 하기 위해 ErrorController 상속 받아 구현 함 (정확히는 controller 에 별도 에러 핸들러 가 없다면 그때는 모두 이곳 으로 진입)
    //해당 Controller 가 없다면 스프링 이 내부 디폴트 로직에 따라 "/error" 리소내 errcode.html 로 자동 매핑 해준다.

    @RequestMapping("/error") //프로퍼티 내 server.error.path 와 동일한 값으로 설정
    public Object handleError(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // 발생한 에러를 직접 찾아 throw 할수도 있지만..  ex가 비어 들어오는 경우가 있음
        // 그래서 status 를 기준으로 ex를 생성하는 방식으로 처리함 (NOT_FOUND 상황의 경우 status 는 404 이나 ex가 비어있음)
        Integer errorStatuscode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorRequestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String requestMethod = request.getMethod();
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        HttpStatus httpStatus = (errorStatuscode != null ? HttpStatus.valueOf(errorStatuscode) : HttpStatus.INTERNAL_SERVER_ERROR);

        // 상위 레벨 에서 발생할 수 있는 에러의 종류를 이정도 로 정의함(더 구체화 가능)
        switch (httpStatus) {
            case NOT_FOUND -> throw new NoHandlerFoundException(requestMethod, errorRequestUri, new HttpHeaders());
            case METHOD_NOT_ALLOWED -> throw new HttpRequestMethodNotSupportedException(requestMethod);
            case UNAUTHORIZED -> throw new AuthenticationException(errorMessage) {};
            case FORBIDDEN -> throw new AccessDeniedException(errorMessage != null ? errorMessage : "Forbidden");
            default -> throw new ResponseStatusException(httpStatus, (errorMessage != null ? errorMessage : httpStatus.getReasonPhrase()));
        }
    }
}

