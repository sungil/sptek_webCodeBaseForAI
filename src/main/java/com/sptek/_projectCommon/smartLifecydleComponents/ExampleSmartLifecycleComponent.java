package com.sptek._projectCommon.smartLifecydleComponents;

import com.sptek.__webFramework.data.mybatis.MyBatisCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExampleSmartLifecycleComponent implements SmartLifecycle {
    // todo :SmartLifecycle IF 구성을 통해 해당 빈의 생명 주기를 조정할 수 있다 (모든 일반 빈들이 생성된 이후 생성하며 모든 빈들이 destroy 전에 destroy 할수 있고 동기/비동기로 처리가능
    // contextRefreshedEvent는 SmartLifecycle를 포함하는 모든 빈이 생성된 이후 발생되며 contextClosedEvent는 SmartLifecycle를 포함하는 모든 빈이 살아 있을때 먼저 발생된다.

    private final MyBatisCommonDao myBatisCommonDao; // ex: 필요한 Bean 을 생성자를 통해 주입 받는다
    private volatile boolean isRunning = false;


    @Override
    // todo: spring 이 start() 호출전 isAutoStartup() 을 확인하여 true 일 star() 해준다.
    // 이미 다른 일반 Bean 은 로딩된 상태 이기 때문에 isAutoStartup() 내부 로직 구현을 통해 동적 실행을 구성 할 수 있다.
    public boolean isAutoStartup() {
        //log.debug("isAutoStartup()");
        return true;
    }

    @Override
    // todo: SmartLifecycle 구성 Bean들 간 phase가 낮을수록 먼저 start 되고 나중에 stop 됨
    public int getPhase() {
        int phase = 0;
        //log.debug("getPhase(): " + phase);
        return phase;
    }

    @Override
    public void start() {
        // todo: 생성하며 해야 할 처리가 있다면 처리함
        isRunning = true;
        //log.debug("start(): " + isRunning);
    }

    @Override
    // spring 이 호출해 주는 실제 stop
    public void stop(Runnable callback) {
        //log.debug("stop(Runnable callback)");
        stop(); // 실제 종료처리가 메소드, callback.run(); 과 순서 변경기 비동기적 종료 처리 가능(그럴경우 다른 bean의 상태를 보장 받을 수 없음)
        callback.run(); // 반드시 호출되어야 Spring context 가 정상 종료됨
    }

    @Override
    public void stop() {
        // todo: 종료되며 해야 할 처리가 있다면 처리함
        //log.debug("stop()");
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        //log.debug("isRunning(): " + isRunning);
        return isRunning;
    }
}
