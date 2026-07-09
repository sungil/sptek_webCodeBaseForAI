package com.sptek.__webFramework.observability.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.sptek.__webFramework.core.constant.CommonConstants;

/**
 * 프레임워크 로그 중 console 제외 marker가 있는 이벤트를 Logback appender에서 거르는 filter.
 *
 * <p>Logback 초기화 시점과 classloader 특성상 Spring Bean이나 프레임워크 런타임 레지스트리에 의존하지 않고,
 * 순수 문자열 marker만 기준으로 판단한다.</p>
 */
public class LogbackFilterForExcept extends Filter<ILoggingEvent>{

    /**
     * 프레임워크 로그 첫 줄에 no-console marker가 있으면 현재 appender 출력을 거부한다.
     */
    @Override
    public FilterReply decide(ILoggingEvent event) {
        // todo: (중요) logback filter에서는 스프링 관련 코드, static 값, bean을 직접 참조하지 말 것! (별도의 classLoader 가 사용 되는 듯)
        // MainClassAnnotationRegister 등 static 클레스 사용 하지 말것 (서로 다른 공간에서 instance 화 되는 듯)
        // 아래 코드는 연산 최적화를 고려해 놓았음 (가능한 변경 하지 말것)

        String msg = event.getFormattedMessage();
        if (msg.startsWith(CommonConstants.FW_LOG_PREFIX)) {
            int newlineIndex = msg.indexOf('\n');
            String firstLine = newlineIndex >= 0 ? msg.substring(0, newlineIndex) : msg;
            if (firstLine.contains(CommonConstants.FW_LOG_NO_CONSOLE_MARK)) return FilterReply.DENY;
        }
        return FilterReply.ACCEPT;
    }
}
