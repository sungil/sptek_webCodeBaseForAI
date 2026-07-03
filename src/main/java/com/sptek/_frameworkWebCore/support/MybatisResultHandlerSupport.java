package com.sptek._frameworkWebCore.support;

/**
 * {@code MyBatisCommonDao.selectListWithResultHandler()}에서 row 단위 처리를 위임받는 추상 핸들러.
 *
 * <p>대량 조회 결과를 한 번에 List로 보관하지 않고 row별로 처리할 때 사용한다. 구현체는
 * {@link #handleResultRow(Object)}를 정의하고 필요하면 {@link #open()}, {@link #close()}를 재정의한다.</p>
 */
public abstract class MybatisResultHandlerSupport <T, R> {

    private boolean stopFlag = false;

    /**
     * row 처리 시작 전에 필요한 준비 작업을 수행한다.
     */
    public void open(){
    }

    /**
     * 더 이상 row 처리가 필요 없을 때 호출해 조회 루프 중단 의사를 표시한다.
     */
    public void stop() {
        this.stopFlag = true;
    }

    /**
     * 조회 루프 중단 요청 여부를 반환한다.
     */
    public boolean isStop() {
        return stopFlag;
    }

    /**
     * row 처리 종료 후 정리 작업을 수행한다.
     */
    public void close(){
    }

    /**
     * 조회 결과 한 row를 처리하고 구현체가 정의한 결과 값을 반환한다.
     */
    public abstract R handleResultRow(T resultRow);
}
