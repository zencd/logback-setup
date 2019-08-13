package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Appender automatically dispatching logging to its children appenders created on demand.
 */
public class MultiAppender extends AppenderBase<ILoggingEvent> {

    private final Map<String, Appender<ILoggingEvent>> appenderByMethod = new HashMap<>();

    private final Appender<ILoggingEvent> defaultAppender;
    private final Function<String, Appender<ILoggingEvent>> createAppender;

    public MultiAppender(Function<String, Appender<ILoggingEvent>> createAppender, Appender<ILoggingEvent> defaultAppender) {
        this.createAppender = createAppender;
        this.defaultAppender = defaultAppender;
    }

    @Override
    protected void append(ILoggingEvent event) {
        Map<String, String> mdc = event.getMDCPropertyMap();
        if (mdc != null) {
            String method = mdc.get(RuntimeLogging.MDC_KEY_METHOD);
            if (method != null) {
                Appender<ILoggingEvent> appender = getOrCreateAppender(method);
                appender.doAppend(event);
            } else {
                defaultAppender.doAppend(event);
            }
        } else {
            defaultAppender.doAppend(event);
        }
    }

    private Appender<ILoggingEvent> getOrCreateAppender(String method) {
        Appender<ILoggingEvent> appender = appenderByMethod.get(method);
        if (appender == null) {
            appender = createAppender.apply(method);
            appenderByMethod.put(method, appender);
        }
        return appender;
    }
}
