package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import org.slf4j.MDC;

public class RuntimeLayout extends LayoutBase<ILoggingEvent> {

    public String doLayout(ILoggingEvent event) {
        return formatLogEntry(event, getContext());
    }

    public static String formatLogEntry(ILoggingEvent event, Context context) {
        String pattern = "[[ method=not-implemented, %m ]]%n";
        return formatXXX(event, context, pattern);

        //StringBuilder buf = new StringBuilder();
        //buf.append("thread: ")
        //        .append(Thread.currentThread().getName())
        //        .append(", ")
        //        .append(MDC.get("method"))
        //        .append(", ")
        //        .append(event.getFormattedMessage())
        //        .append(CoreConstants.LINE_SEPARATOR);
        //return buf.toString();
    }

    static String formatXXX(ILoggingEvent event, Context context, String pattern) {
        RuntimePatternLayout layoutImpl = new RuntimePatternLayout();
        layoutImpl.setPattern(pattern);
        layoutImpl.setContext(context);
        layoutImpl.start();
        String s = layoutImpl.doLayout(event);
        layoutImpl.stop();
        return s;
    }

}
