package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

public class MethodConverter extends ClassicConverter {
    public String convert(ILoggingEvent event) {
        String method = MDC.get(RuntimeLogging.MDC_KEY_METHOD);
        return method != null ? method : "?";
    }
}
