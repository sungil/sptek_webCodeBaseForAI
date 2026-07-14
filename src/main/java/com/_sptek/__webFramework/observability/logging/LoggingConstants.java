package com._sptek.__webFramework.observability.logging;

/**
 * 프레임워크 로깅 기능에서 공유하는 로그 포맷 마커와 request attribute 이름을 모아둔다.
 */
public final class LoggingConstants {
    public static final String FW_LOG_PREFIX = "■ FW_LOG : ";
    public static final String FW_LOG_BASE_DIR = "_FW_LOG";
    public static final String FW_LOG_FILENAME_MARK = "file->";
    public static final String FW_LOG_NO_CONSOLE_MARK = "->noConsole";
    public static final String FW_START_LOG_TAG = "FW_START_LOG file->FW_START_LOG";
    public static final String SERVER_INITIALIZATION_MARK = "FRAMEWORK BEAN INITIALIZATION NOTICE : ";

    public static final String REQ_ATTRIBUTE_FOR_LOGGING_TIMESTAMP = "REQ_ATTRIBUTE_FOR_LOGGING_TIMESTAMP";
    public static final String REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW = "REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW";
    public static final String REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE = "REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE";
    public static final String REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS = "REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS";
    public static final String REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED = "REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_ENABLED";
    public static final String REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG = "REQ_ATTRIBUTE_FOR_REQ_RES_DETAIL_LOG_TAG";
    public static final String REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE = "REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE";
    public static final String REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_CORRELATION_ID = "REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_CORRELATION_ID";

    public static final String VISIT_HISTORY_COOKIE_NAME = "VISIT_HISTORY";
    public static final String VISIT_HISTORY_COOKIE_VALUE = "OK";
    public static final String VISIT_HISTORY_NEW_VISITOR_LOG = "NEW_VISITOR";
    public static final String VISIT_HISTORY_EXIST_VISITOR_LOG = "EXIST_VISITOR";

    private LoggingConstants() {
    }
}
