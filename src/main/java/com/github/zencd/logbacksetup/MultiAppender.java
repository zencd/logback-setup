package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Custom appender.
 * Automatically dispatches logging to a number of children appenders created on demand.
 * Acts much like {@link ch.qos.logback.classic.sift.SiftingAppender}.
 */
public class MultiAppender extends AppenderBase<ILoggingEvent> {

    private final Map<String, Appender<ILoggingEvent>> appenderByMethod = new HashMap<>();

    private final Appender<ILoggingEvent> defaultAppender;
    private final Function<String, Appender<ILoggingEvent>> createAppender;
    private final CustomLogging logging;

    public MultiAppender(CustomLogging logging, Function<String, Appender<ILoggingEvent>> createAppender, Appender<ILoggingEvent> defaultAppender) {
        this.logging = logging;
        this.createAppender = createAppender;
        this.defaultAppender = defaultAppender;
    }

    @Override
    protected void append(ILoggingEvent event) {
        Map<String, String> mdc = event.getMDCPropertyMap();
        if (mdc != null) {
            String method = mdc.get(CustomLogging.MDC_KEY_METHOD);
            if (method != null) {
                if (logging.filterEventByMethod(event, method)) {
                    Appender<ILoggingEvent> appender = getOrCreateAppender(method);
                    appender.doAppend(event);
                }
            } else {
                defaultAppender.doAppend(event);
            }
        } else {
            defaultAppender.doAppend(event);
        }
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        stopAppenders();
    }

    private void stopAppenders() {
        // `defaultAppender` seems should not be stopped here
        appenderByMethod.values().forEach((appender) -> {
            if (appender.isStarted()) {
                appender.stop();
            }
        });
    }

    private Appender<ILoggingEvent> getOrCreateAppender(String method) {
        Appender<ILoggingEvent> appender = appenderByMethod.get(method);
        if (appender == null) {
            synchronized (appenderByMethod) {
                appender = appenderByMethod.get(method);
                if (appender == null) {
                    appender = createAppender.apply(method);
                    appenderByMethod.put(method, appender);
                }
            }
        }
        return appender;
    }
}
