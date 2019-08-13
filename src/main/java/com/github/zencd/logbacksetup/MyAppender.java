package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAppender extends AppenderBase<ILoggingEvent> {

    private final Map<String, Appender<ILoggingEvent>> appenderByMethod = new HashMap<>();

    private final LoggerContext lc;
    private final Encoder<ILoggingEvent> encoder;
    private final List<Filter<ILoggingEvent>> filters;
    private final Appender<ILoggingEvent> defaultAppender;

    public MyAppender(LoggerContext lc, Encoder<ILoggingEvent> encoder, List<Filter<ILoggingEvent>> filters, Appender<ILoggingEvent> defaultAppender) {
        this.lc = lc;
        this.encoder = encoder;
        this.filters = filters;
        this.defaultAppender = defaultAppender;
    }

    @Override
    protected void append(ILoggingEvent event) {
        Map<String, String> mdc = event.getMDCPropertyMap();
        if (mdc != null) {
            String method = mdc.get("method");
            //System.out.println("mdc: " + mdc);
            if (method != null) {
                Appender<ILoggingEvent> appender = getOrCreateAppender(method);
                appender.doAppend(event);
            } else {
                //System.err.println("no method in mdc - calling defaultAppender");
                defaultAppender.doAppend(event);
            }
        } else {
            System.err.println("no mdc");
        }
    }

    private Appender<ILoggingEvent> getOrCreateAppender(String method) {
        Appender<ILoggingEvent> appender = appenderByMethod.get(method);
        if (appender == null) {
            FileAppender fileAppender = new FileAppender<ILoggingEvent>();
            appender = fileAppender;
            fileAppender.setContext(lc);
            fileAppender.setAppend(false);
            fileAppender.setEncoder(encoder);
            fileAppender.setFile(method + ".log");
            for (Filter<ILoggingEvent> filter : filters) {
                fileAppender.addFilter(filter);
            }
            fileAppender.start();
            appenderByMethod.put(method, fileAppender);
        }
        return appender;
    }
}
