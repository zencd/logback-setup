package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class MyFilter extends Filter<ILoggingEvent> {
    private final RuntimeLogging logging;

    @Deprecated
    public MyFilter() {
        logging = null;
        throw new RuntimeException("this class is not a bean, no default constructor here");
    }

    public MyFilter(RuntimeLogging logging) {
        this.logging = logging;
    }

    public FilterReply decide(ILoggingEvent event) {
        return RuntimeLogging.INSTANCE.filterEvent(event);
    }
}
