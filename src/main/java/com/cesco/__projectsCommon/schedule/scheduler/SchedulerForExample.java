package com.cesco.__projectsCommon.schedule.scheduler;

import org.springframework.stereotype.Component;

@Component
public class SchedulerForExample {

    /**
     * 애플리케이션 시작 후 5초 뒤 첫 실행
     * 이후 10초 간격(이전 실행 완료 후 10초 기다림)으로 실행
     * 기터 설정은 CommonSchedulerExecutorConfig 설정으로 동작
     */
    //@Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void runTask() {
        // do what you want.
    }
}
