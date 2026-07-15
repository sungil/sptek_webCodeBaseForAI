package com._sptek.__webFramework.observability.logging;

import com._sptek.__webFramework.core.exception.ThrowableUnwrapSupport;
import com._sptek.__webFramework.bootstrap.registry.MainClassAnnotationRegister;
import com._sptek.__webFramework.core.exception.ServiceException;
import com._sptek.__webFramework.observability.timing.Enable_ExecutionTimeLog_At_Main;
import com._sptek.__webFramework.observability.timing.RequestDurationDto;
import com._sptek.__webFramework.web.util.RequestUtil;
import com._sptek.__webFramework.web.util.ResponseUtil;
import com._sptek.__webFramework.core.util.TypeConvertUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
/**
 * 프레임워크 공통 로그 포맷과 예외/요청-응답 상세 로그 출력을 제공하는 유틸리티.
 *
 * <p>{@link LoggingConstants#FW_LOG_PREFIX}를 기준으로 로그 모양을 통일하며, 일부 메서드는
 * SLF4J {@link LoggingEventBuilder}를 사용해 로그 레벨이 비활성화된 경우 메시지 계산 비용을 줄인다.</p>
 */
public class LoggingUtil {

    /**
     * 기본 로그 태그 없이 한 줄 로그 포맷을 만든다.
     */
    public static String makeSimpleForm(String content) {
        return makeSimpleForm("", content);
    }

    /**
     * 프레임워크 prefix, 선택 로그 태그, 본문을 한 줄 로그 포맷으로 결합한다.
     */
    public static String makeSimpleForm(String logTag, String content) {
        // NOTE: 아래 형태 변경시 다른 코드에 영향이 있음
        return "%s%s => %s".formatted(LoggingConstants.FW_LOG_PREFIX, logTag, LoggingUtil.removeLastNewline(content));
    }

    /**
     * 기본 로그 태그 없이 제목과 본문을 포함한 박스형 로그 포맷을 만든다.
     */
    public static String makeBaseForm(String title, String content) {
        return makeBaseForm("", title, content);
    }

    /**
     * 프레임워크 prefix, 선택 로그 태그, 제목, 본문을 박스형 로그 포맷으로 결합한다.
     */
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
                .formatted(LoggingConstants.FW_LOG_PREFIX, logTag, title, LoggingUtil.removeLastNewline(content));
    }

    /**
     * 로그 박스 하단 공백이 늘어나지 않도록 마지막 개행 하나만 제거한다.
     */
    private static String removeLastNewline(String string) {
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

    /**
     * Supplier 인자를 지연 평가할 수 있는 한 줄 로그를 지정 레벨로 출력한다.
     *
     * <p>args에는 {@code Supplier<?>}와 일반 값을 섞어 넘길 수 있다.</p>
     */
    public static void logSimpleForm(Logger logger, Level level, String logTag, String bodyTemplate, Object... args) {
        if (!logger.isEnabledForLevel(level)) return;

        LoggingEventBuilder loggingEventBuilder = logger.atLevel(level).setMessage(SIMPLE_FORM + removeLastNewline(bodyTemplate))
                .addArgument(LoggingConstants.FW_LOG_PREFIX)
                .addArgument(logTag);

        // 바디 인자들: Supplier면 lazy, 아니면 즉시 값으로 처리
        for (Object object : args) {
            if (object instanceof java.util.function.Supplier<?> supplier) loggingEventBuilder.addArgument(supplier);
            else loggingEventBuilder.addArgument(object);
        }
        loggingEventBuilder.log();
    }

    /**
     * Supplier 인자를 지연 평가할 수 있는 박스형 로그를 지정 레벨로 출력한다.
     */
    public static void logBaseForm(Logger logger, Level level, String logTag, String title, String bodyTemplate, Object... args) {
        if (!logger.isEnabledForLevel(level)) return;

        LoggingEventBuilder loggingEventBuilder = logger.atLevel(level).setMessage(BASE_FORM_HEADER + bodyTemplate + BASE_FORM_BOTTOM)
                .addArgument(LoggingConstants.FW_LOG_PREFIX)
                .addArgument(logTag)
                .addArgument(title);

        // 바디 인자들: Supplier면 lazy, 아니면 즉시 값으로 처리
        for (Object object : args) {
            if (object instanceof java.util.function.Supplier<?> supplier) loggingEventBuilder.addArgument(supplier);
            else loggingEventBuilder.addArgument(object);
        }
        loggingEventBuilder.log();
    }

    /**
     * 래핑된 예외를 실제 원인 예외로 풀어 공통 규칙으로 로깅한다.
     */
    public static void exLogging(Logger logger, Exception ex) {
        exLoggingAndReturnThrowable(logger, ex);
    }

    /**
     * 래핑된 예외를 실제 원인 예외로 풀어 로깅하고, 호출자가 후속 처리할 수 있도록 반환한다.
     */
    public static Throwable exLoggingAndReturnThrowable(Logger logger, Exception ex) {
        Throwable t = ThrowableUnwrapSupport.getRealException(ex);
        String tag = t instanceof ServiceException ? "ServiceException occurred" : "Exception occurred";

        if (logger.isDebugEnabled()) {
            logger.error("{}: {}", tag, t.getMessage(), t);
        } else {
            logger.error("{}: {}", tag, t.getMessage());
        }
        return t;
    }

    /**
     * 응답 DTO 없이 request/response wrapper 기준으로 요청-응답 상세 로그를 남긴다.
     */
    public static void reqResDetailLogging(Logger logger, HttpServletRequest request, HttpServletResponse response, String title) throws IOException {
        reqResDetailLogging(logger, request, response, null, title);
    }

    /**
     * 현재 HTTP 요청의 URL, 파라미터, 헤더, 본문, 응답, outbound 연계 정보를 한 번에 로깅한다.
     */
    public static void reqResDetailLogging(Logger logger, HttpServletRequest request, HttpServletResponse response, @Nullable Object responseBodyDto, String title) throws IOException {
        if (!logger.isEnabledForLevel(Level.INFO)) return;

        // main 과 controller 쪽 양쪽에 적용되어 있는 경우 ReqResDetailLogDecisionInterceptor 가 controller 쪽 값을 우선하여 저장한다.
        String logTag = StringUtils.hasText(Objects.toString(request.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG), ""))
                ? Objects.toString(request.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG), "")
                : Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class).get("value"), "");

        String sessionId = Optional.ofNullable(request.getSession(false))
                .map(HttpSession::getId)
                .orElse("");
        String methodType = RequestUtil.getRequestMethodType(request);
        String url = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) != null ? RequestUtil.getRequestDomain(request) + (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) : RequestUtil.getRequestUrlQuery(request);
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(request));
        String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(request, "|"));
        String requestBody = request instanceof ContentCachingRequestWrapper contentCachingRequestWrapper ? RequestUtil.getRequestBody(contentCachingRequestWrapper) : "";

        String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(response, "|"));
        String relatedOutbounds = Optional.ofNullable(request.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS)).map(Object::toString).orElse("");
        String requestTime = "";
        String responseTime = "";
        String durationMsec = "";
        if (MainClassAnnotationRegister.hasAnnotation(Enable_ExecutionTimeLog_At_Main.class)) {
            RequestDurationDto requestDurationDto = RequestUtil.traceRequestDuration();
            requestTime = requestDurationDto.getStartTime();
            responseTime = requestDurationDto.getCurrentTime();
            durationMsec = requestDurationDto.getDurationMsec();
        }
        String exceptionMsgForView = Optional.ofNullable(request.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE)).map(Object::toString).orElse("");
        int responseStatus = response.getStatus();

        // View 요청의 경우 Response body는 html 페이지임으로 제외 함
        String responseBody = "";
        if (responseBodyDto != null) responseBody = TypeConvertUtil.objectToJsonWithoutRootName(responseBodyDto, true);
        else if (response instanceof ContentCachingResponseWrapper contentCachingResponseWrapper && RequestUtil.isApiRequest(request)) responseBody = ResponseUtil.getResponseBody(contentCachingResponseWrapper);

        String modelAndView = Optional.ofNullable(request.getAttribute(LoggingConstants.REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW)).map(Object::toString).orElse("");

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
                responseStatus: %s
                modelAndView(view): %s
                responseBody: %s
                """
                .formatted(sessionId, methodType, url, params, requestHeader, requestBody, responseHeader, relatedOutbounds
                        , requestTime, responseTime, durationMsec, exceptionMsgForView, responseStatus, modelAndView, responseBody);
        log.info(LoggingUtil.makeBaseForm(logTag, title, logContent));
    }
}


