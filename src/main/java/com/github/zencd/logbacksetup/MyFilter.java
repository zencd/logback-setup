package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MyFilter extends Filter<ILoggingEvent> {
    public FilterReply decide(ILoggingEvent event) {
        return RuntimeLogging.INSTANCE.filterEvent(event);
    }
}
