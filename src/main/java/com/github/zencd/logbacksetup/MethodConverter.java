package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

class MethodConverter extends ClassicConverter {
    public String convert(ILoggingEvent event) {
        return "---method-name---";
    }
}

