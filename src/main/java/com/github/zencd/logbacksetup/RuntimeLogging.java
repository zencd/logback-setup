package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RuntimeLogging {

    private final Map<String, Level> levelByMethod = new HashMap<>();
    {
        levelByMethod.put("method1", Level.INFO);
        levelByMethod.put("method2", Level.ERROR);
    }

    public static final String MDC_KEY_METHOD = "method";

    public static final RuntimeLogging INSTANCE = new RuntimeLogging();

    private RuntimeLogging() {}

    public void configure() throws JoranException {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset(); //  reset prev config
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        configurator.doConfigure("logback-config-01.xml");
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        //ch.qos.logback.classic.Logger root = lc.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        //root.setLevel(Level.INFO); // override level in XML
    }

    public FilterReply filterEvent(ILoggingEvent event) {
        String method = event.getMDCPropertyMap().get(MDC_KEY_METHOD);
        if (method != null) {
            Level minLevel = levelByMethod.get(method);
            if (minLevel != null) {
                if (event.getLevel().levelInt < minLevel.levelInt) {
                    return FilterReply.DENY;
                }
            }
        }
        return FilterReply.NEUTRAL;
    }
}
