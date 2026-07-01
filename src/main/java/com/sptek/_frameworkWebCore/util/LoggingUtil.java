package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore._annotation.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class LoggingUtil {

    public static String makeSimpleForm(String content) {
        return makeSimpleForm("", content);
    }

    public static String makeSimpleForm(String logTag, String content) {
        // 변경시 주의(아래 형태가 다른 코드에 영향이 있음)
        return "%s%s => %s".formatted(CommonConstants.FW_LOG_PREFIX, logTag, LoggingUtil.removeLastNewline(content));
    }

    public static String makeBaseForm(String title, String content) {
        return makeBaseForm("", title, content);
    }

    public static String makeBaseForm(String logTag, String title, String content) {
        // 변경시 주의(아래 형태가 다른 코드에 영향이 있음)
        return """
                %s%s
                --------------------------------------------------------------------------------
                [ **** %s **** ]
                --------------------------------------------------------------------------------
                %s
                --------------------------------------------------------------------------------
                """
                .formatted(CommonConstants.FW_LOG_PREFIX, logTag, title, LoggingUtil.removeLastNewline(content));
    }

    public static String removeLastNewline(String string) {
        if (string != null && string.endsWith("\n")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    // Lazy 로깅을 처리하기 할때 사용 (성능 향상) ---------------------------------------------------------------------------------------------
    private static final String SIMPLE_FORM = "{}{} => ";
    private static final String BASE_FORM_HEADER = """
            {}{}
            --------------------------------------------------------------------------------
            [ **** {} **** ]
            --------------------------------------------------------------------------------
            """;
    private static final String BASE_FORM_BOTTOM = """
            --------------------------------------------------------------------------------
            """;

    // args에는 Supplier<?> 또는 값(Object)을 섞어서 넘겨도 됨 (lazy + eager 혼용)
    public static void logSimpleForm(Logger logger, Level level, String logTag, String bodyTemplate, Object... args) {
        if (!logger.isEnabledForLevel(level)) return;

        LoggingEventBuilder loggingEventBuilder = logger.atLevel(level).setMessage(SIMPLE_FORM + removeLastNewline(bodyTemplate))
                .addArgument(CommonConstants.FW_LOG_PREFIX)
                .addArgument(logTag);

        // 바디 인자들: Supplier면 lazy, 아니면 즉시 값으로 처리
        for (Object object : args) {
            if (object instanceof java.util.function.Supplier<?> supplier) loggingEventBuilder.addArgument(supplier);
            else loggingEventBuilder.addArgument(object);
        }
        loggingEventBuilder.log();
    }

    public static void logBaseForm(Logger logger, Level level, String logTag, String title, String bodyTemplate, Object... args) {
        if (!logger.isEnabledForLevel(level)) return;

        LoggingEventBuilder loggingEventBuilder = logger.atLevel(level).setMessage(BASE_FORM_HEADER + bodyTemplate + BASE_FORM_BOTTOM)
                .addArgument(CommonConstants.FW_LOG_PREFIX)
                .addArgument(logTag)
                .addArgument(title);

        // 바디 인자들: Supplier면 lazy, 아니면 즉시 값으로 처리
        for (Object object : args) {
            if (object instanceof java.util.function.Supplier<?> supplier) loggingEventBuilder.addArgument(supplier);
            else loggingEventBuilder.addArgument(object);
        }
        loggingEventBuilder.log();
    }

    public static void exLogging(Logger logger, Exception ex) {
        exLoggingAndReturnThrowable(logger, ex);
    }

    public static Throwable exLoggingAndReturnThrowable(Logger logger, Exception ex) {
        Throwable t = ExceptionUtil.getRealException(ex);
        String tag = t instanceof ServiceException ? "ServiceException occurred" : "Exception occurred";

        if (logger.isDebugEnabled()) {
            logger.error("{}: {}", tag, t.getMessage(), t);
        } else {
            logger.error("{}: {}", tag, t.getMessage());
        }
        return t;
    }

    public static void reqResDetailLogging(Logger logger, HttpServletRequest request, HttpServletResponse response, String title) throws IOException {
        reqResDetailLogging(logger, request, response, null, title);
    }

    public static void reqResDetailLogging(Logger logger, HttpServletRequest request, HttpServletResponse response, @Nullable Object responseBodyDto, String title) throws IOException {
        if (!logger.isEnabledForLevel(Level.INFO)) return;

        // main 과 controller 쪽 양쪽에 적용되어 있는 경우 ReqResDetailLogDecisionInterceptor 가 controller 쪽 값을 우선하여 저장한다.
        String logTag = StringUtils.hasText(Objects.toString(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG), ""))
                ? Objects.toString(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG), "")
                : Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class).get("value"), "");

        String sessionId = request.getSession().getId();
        String methodType = RequestUtil.getRequestMethodType(request);
        String url = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) != null ? RequestUtil.getRequestDomain(request) + (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) : RequestUtil.getRequestUrlQuery(request);
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(request));
        String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(request, "|"));
        String requestBody = request instanceof ContentCachingRequestWrapper contentCachingRequestWrapper ? RequestUtil.getRequestBody(contentCachingRequestWrapper) : "";

        String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(response, "|"));
        String relatedOutbounds = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS)).map(Object::toString).orElse("");
        String requestTime = RequestUtil.traceRequestDuration().getStartTime();
        String responseTime = RequestUtil.traceRequestDuration().getCurrentTime();
        String durationMsec = RequestUtil.traceRequestDuration().getDurationMsec();
        String exceptionMsgForView = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE)).map(Object::toString).orElse("");
        int responseStatus = response.getStatus();
        String isAsyncDispatch = request.getDispatcherType() == DispatcherType.ASYNC ? "Async Response" : "Sync Response";

        // View 요청의 경우 Response body는 html 페이지임으로 제외 함
        String responseBody = "";
        if (responseBodyDto != null) responseBody = TypeConvertUtil.objectToJsonWithoutRootName(responseBodyDto, true);
        else if (response instanceof ContentCachingResponseWrapper contentCachingResponseWrapper && RequestUtil.isApiRequest(request)) responseBody = ResponseUtil.getResponseBody(contentCachingResponseWrapper);

        String modelAndView = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW)).map(Object::toString).orElse("");

        String logContent = """
                sessionId: %s
                (%s) url: %s
                params: %s
                requestHeader: %s
                requestBody: %s
                responseHeader: %s
                relatedOutbounds: %s
                requestTime: %s
                responseTime(real): %s
                durationMsec(real): %s
                exceptionMsg(view): %s
                responseStatus: %s, %s
                modelAndView(view): %s
                responseBody: %s
                """
                .formatted(sessionId, methodType, url, params, requestHeader, requestBody, responseHeader, relatedOutbounds
                        , requestTime, responseTime, durationMsec, exceptionMsgForView, responseStatus, isAsyncDispatch, modelAndView, responseBody);
        log.info(LoggingUtil.makeBaseForm(logTag, title, logContent));
    }
}

