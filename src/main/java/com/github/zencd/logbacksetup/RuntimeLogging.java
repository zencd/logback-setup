package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuntimeLogging {

    static Action BASE_CLASS_FOR_XML_CONFIGURATORS;

    private final Map<String, Level> levelByMethod = new HashMap<>() {{
        put("someMethod", Level.INFO);
        put("anotherMethod", Level.ERROR);
    }};

    private final Map<String, String> logFileByMethod = new HashMap<>() {{
        put("someMethod", "method1.log");
        put("anotherMethod", "method2.log");
    }};

    private final String logFileNameNoMethod = "default.log";

    public static final String MDC_KEY_METHOD = "method";

    public static final RuntimeLogging INSTANCE = new RuntimeLogging();

    private RuntimeLogging() {}

    public void configure() throws JoranException {
        configurePureJava();
        //configureXmlBased();
    }

    public void configurePureJava() throws JoranException {
        Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        LoggerContext lc = rootLogger.getLoggerContext();
        // we are not interested in auto-configuration
        lc.reset();
        rootLogger.setLevel(Level.DEBUG);

        //rootLogger.getAppender()

        configureJavaPatternRules(lc);

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setPattern("%-5level %met { %thread }: %message%n");
        encoder.start();

        MyFilter filter = new MyFilter(this);
        filter.start();

        List<MyFilter> allFilters = List.of(filter);

        ConsoleAppender<ILoggingEvent> defaultAppender = new ConsoleAppender<ILoggingEvent>();
        defaultAppender.setContext(lc);
        defaultAppender.setEncoder(encoder);
        defaultAppender.addFilter(filter);
        defaultAppender.start();

        MyAppender discriminatingAppender = new MyAppender((method) -> createAppender(lc, encoder, allFilters, method), defaultAppender);
        discriminatingAppender.start();

        rootLogger.addAppender(discriminatingAppender);
        //rootLogger.addAppender(defaultAppender);
    }

    private void configureJavaPatternRules(LoggerContext lc) {
        Map<String, String> ruleRegistry = (Map) lc.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        //System.out.println("ruleRegistry: " + ruleRegistry);
        if (ruleRegistry == null) {
            ruleRegistry = new HashMap<String, String>();
            lc.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
        }
        ruleRegistry.put("met", "com.github.zencd.logbacksetup.MethodConverter");
        //System.out.println("ruleRegistry: " + ruleRegistry);
    }

    public void configureXmlBased() throws JoranException {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset(); //  reset prev config
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        configurator.doConfigure("logback-config-file.xml");
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        System.out.println("DEBUG: " + rootLogger.getAppender("STDOUT"));
        System.out.println("FILE1: " + rootLogger.getAppender("FILE1"));
        System.out.println("FILE2: " + rootLogger.getAppender("FILE2"));

        //ch.qos.logback.classic.Logger root = lc.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        //root.setLevel(Level.INFO); // override level in XML
    }

    FilterReply filterEvent(ILoggingEvent event) {
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

    private Appender<ILoggingEvent> createAppender(LoggerContext lc, Encoder<ILoggingEvent> encoder, List<MyFilter> filters, String methodName) {
        FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
        appender.setContext(lc);
        appender.setAppend(false);
        appender.setEncoder(encoder);
        appender.setFile(logFileByMethod.getOrDefault(methodName, logFileNameNoMethod));
        for (Filter<ILoggingEvent> filter : filters) {
            appender.addFilter(filter);
        }
        appender.start();
        return appender;
    }
}
