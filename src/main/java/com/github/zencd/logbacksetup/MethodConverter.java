package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Map;

/**
 * Converter is a thing that expands "%hello" within a logging format into actual value.
 */
public class MethodConverter extends ClassicConverter {
    public String convert(ILoggingEvent event) {
        Map<String, String> mdc = event.getMDCPropertyMap();
        if (mdc != null) {
            String method = mdc.get(CustomLogging.MDC_KEY_METHOD);
            if (method != null) {
                return "method=" + method;
            }
        }
        return "method?";
    }
}
