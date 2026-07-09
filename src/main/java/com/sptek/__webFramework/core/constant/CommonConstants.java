package com.sptek.__webFramework.core.constant;

import org.springframework.stereotype.Component;

@Component
public class CommonConstants {
    // project
    public static final String PROJECT_PACKAGE_NAME = "com.sptek.";
    public static final String FRAMEWORK_WEBCORE_PACKAGE_NAME = PROJECT_PACKAGE_NAME + "__webFramework.";
    public static final String FRAMEWORK_ANNOTATION_PACKAGE_NAME = FRAMEWORK_WEBCORE_PACKAGE_NAME;

    // Logging
    public static final String FW_LOG_PREFIX = "■ FW_LOG : ";
    public static final String FW_LOG_BASE_DIR = "_FW_LOG";
    public static final String FW_LOG_FILENAME_MARK = "file->";
    public static final String FW_LOG_NO_CONSOLE_MARK = "->noConsole";
    public static final String FW_START_LOG_TAG = "FW_START_LOG file->FW_START_LOG";
    public static final String DEBUGGING_HELP_MESSAGE = "이 메시지가 보인다면!! FW 담당자 에게 알려 주세요!";
    public static final String SERVER_INITIALIZATION_MARK = "FRAMEWORK BEAN INITIALIZATION NOTICE : ";

    // Req, Res, Async, duplication
    public static final String REQ_ATTRIBUTE_FOR_LOGGING_TIMESTAMP = "REQ_ATTRIBUTE_FOR_LOGGING_TIMESTAMP";
    public static final String REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW = "REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW";
    public static final String REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE = "REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE";
    public static final String REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS = "REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS";
    public static final String REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED = "REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED";
    public static final String REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG = "REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG";
    public static final String REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE = "REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE";
    public static final String REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_CORRELATION_ID = "REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_CORRELATION_ID";
    public static final String REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION = "REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION";
    public static final long   DUPLICATION_PREVENT_MAX_MS = 30_000L;
    public static final long   DUPLICATION_PREVENT_MIN_MS = 1_000L;


    // locale
    public static final String LOCALE_COOKIE_NAME = "locale";
    public static final String TIMEZONE_COOKIE_NAME = "timezone";
    public static final int LOCALE_COOKIE_MAX_AGE_DAY = 7; //사용자 가 설정한 로케일 정보 쿠키를 얼마 동안 보존할 것인지(Duration)
    public static final int TIMEZONE_COOKIE_MAX_AGE_DAY = 7; //사용자 가 설정한 로케일 정보 쿠키를 얼마 동안 보존할 것인지(Duration)

    // site visit histoy
    public static final String VISIT_HISTORY_COOKIE_NAME = "VISIT_HISTORY";
    public static final String VISIT_HISTORY_COOKIE_VALE = "OK";
    public static final String VISIT_HISTORY_NEW_VISITOR_LOG = "NEW_VISITOR";
    public static final String VISIT_HISTORY_EXIST_VISITOR_LOG = "EXIST_VISITOR";

    // spring-security
    public static final String ANONYMOUS_USER = "anonymousUser";

    // Thread
    public static final int AVAILABLE_PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();
    public static final int RECOMMEND_THREAD_POOL_SIZE = AVAILABLE_PROCESSOR_COUNT * 2;
    public static final int RECOMMEND_THREAD_POOL_MAX_SIZE = AVAILABLE_PROCESSOR_COUNT * 4;
    public static final int RECOMMEND_THREAD_QUEUE_SIZE = RECOMMEND_THREAD_POOL_MAX_SIZE * 4;

}
