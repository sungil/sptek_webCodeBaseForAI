package com._sptek.__webFramework.api.deduplicationRequest;

/**
 * 중복 요청 방지 기능에서 사용하는 request/session attribute 이름과 기본 시간 정책을 모아둔다.
 */
public final class DeduplicationRequestConstants {
    public static final String REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION = "REQ_ATTRIBUTE_FOR_CHECKING_DUPLICATION";
    public static final long DUPLICATION_PREVENT_MAX_MS = 30_000L;
    public static final long DUPLICATION_PREVENT_MIN_MS = 1_000L;

    private DeduplicationRequestConstants() {
    }
}
