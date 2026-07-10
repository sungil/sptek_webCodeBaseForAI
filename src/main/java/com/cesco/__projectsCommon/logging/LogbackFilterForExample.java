package com.cesco.__projectsCommon.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


public class LogbackFilterForExample extends Filter<ILoggingEvent>{
    // todo: (중요) logback filter에서는 스프링 관련 코드, static 값, bean을 직접 참조하지 말 것! (별도의 classLoader 가 사용 되는 듯)
    // MainClassAnnotationRegister 등 static 클레스 사용 하지 말것 (서로 다른 공간에서 instance 화 되는 듯)

    String filterKeyword = "example keyword"; //LogBackFilter keywork example.
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().startsWith(filterKeyword)) {
            //조건에 맞을때 log 처리함
            return FilterReply.ACCEPT;
        }else{
            return FilterReply.DENY;
        }
    }
}
