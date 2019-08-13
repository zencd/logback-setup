package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.slf4j.MDC;

import java.util.Map;

public class MethodConverter extends ClassicConverter {
    public String convert(ILoggingEvent event) {
        Map<String, String> mdc = event.getMDCPropertyMap();
        if (mdc != null) {
            String method = mdc.get(RuntimeLogging.MDC_KEY_METHOD);
            if (method != null) {
                return "method=" + method;
            }
        }
        return "method?";
    }
}
