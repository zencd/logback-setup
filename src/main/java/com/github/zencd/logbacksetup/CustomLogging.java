package com.github.zencd.logbacksetup;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public class CustomLogging {

    private final Map<String, Level> levelByMethod = new HashMap<>();

    private final Map<String, String> logFileByMethod = new HashMap<>();

    private final Map<String, String> patternByMethod = new HashMap<>();

    private final Map<String, PatternLayoutEncoder> encoderByPattern = new HashMap<>();

    private final String LOG_FILE_NAME_NO_METHOD = "default.log";

    private static final String DEFAULT_PATTERN = "%d %-5level %message <-- default pattern %n";

    static final String MDC_KEY_METHOD = "CURRENT_METHOD_NAME";

    private static final Map<String, String> CUSTOM_CONVERTERS = new HashMap<String, String>() {{
        put("met", MethodConverter.class.getName());
    }};

    private static final CustomLogging INSTANCE = new CustomLogging();

    private CustomLogging() {
    }

    static void reconfigure() {
        INSTANCE.configureLogback();
    }

    public static void configureMethodsOneWay() {
        INSTANCE.levelByMethod.clear();
        INSTANCE.logFileByMethod.clear();
        INSTANCE.patternByMethod.clear();
        {
            String methodName = "someMethod";
            INSTANCE.levelByMethod.put(methodName, Level.INFO);
            INSTANCE.logFileByMethod.put(methodName, "method1.log");
            INSTANCE.patternByMethod.put(methodName, "PATTERN1 %-5level %met { %thread }: %message%n");
        }
        {
            String methodName = "anotherMethod";
            INSTANCE.levelByMethod.put(methodName, Level.ERROR);
            INSTANCE.logFileByMethod.put(methodName, "method2.log");
            INSTANCE.patternByMethod.put(methodName, "PATTERN2 --x_X-- %-5level { %thread }: %message%n");
        }
    }

    public static void configureMethodsAnotherWay() {
        INSTANCE.levelByMethod.clear();
        INSTANCE.logFileByMethod.clear();
        INSTANCE.patternByMethod.clear();
        {
            String methodName = "someMethod";
            INSTANCE.levelByMethod.put(methodName, Level.DEBUG);
            INSTANCE.logFileByMethod.put(methodName, "method1x.log");
            INSTANCE.patternByMethod.put(methodName, "PATTERN1x %-5level %met: %message%n");
        }
        {
            String methodName = "anotherMethod";
            INSTANCE.levelByMethod.put(methodName, Level.WARN);
            INSTANCE.logFileByMethod.put(methodName, "method2x.log");
            INSTANCE.patternByMethod.put(methodName, "PATTERN2x --x_X-- %-3level %message%n");
        }
    }

    public static void setCurrentMethod(String methodName) {
        MDC.put(MDC_KEY_METHOD, methodName);
    }

    public static void unsetCurrentMethod() {
        MDC.remove(MDC_KEY_METHOD);
    }

    private void configureLogback() {
        clearCachesEtc();

        Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        LoggerContext lc = rootLogger.getLoggerContext();
        lc.reset(); // reconfigure
        rootLogger.setLevel(Level.DEBUG);

        configurePatterns(lc);

        ConsoleAppender<ILoggingEvent> defaultAppender = new ConsoleAppender<ILoggingEvent>();
        {
            defaultAppender.setContext(lc);
            defaultAppender.setEncoder(getOrCreateEncoderByPattern(lc, DEFAULT_PATTERN));
            //defaultAppender.addFilter(filter);
            defaultAppender.start();
        }

        MultiAppender multiAppender = new MultiAppender(this, (method) -> createFileAppender(lc, method), defaultAppender);
        multiAppender.start();

        rootLogger.addAppender(multiAppender);
        //rootLogger.addAppender();
    }

    private void clearCachesEtc() {
        encoderByPattern.clear();
    }

    private void configurePatterns(LoggerContext lc) {
        Map<String, String> ruleRegistry = (Map<String, String>) lc.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        if (ruleRegistry == null) {
            ruleRegistry = new HashMap<String, String>();
            lc.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
        }
        ruleRegistry.putAll(CUSTOM_CONVERTERS);
    }

    boolean filterEventByMethod(ILoggingEvent event, @NotNull String method) {
        Level minLevel = levelByMethod.get(method);
        if (minLevel != null) {
            return event.getLevel().levelInt >= minLevel.levelInt;
        }
        return true;
    }

    private Appender<ILoggingEvent> createFileAppender(LoggerContext lc, String methodName) {
        FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
        appender.setContext(lc);
        appender.setAppend(false);
        appender.setEncoder(getOrCreateEncoderByMethod(lc, methodName));
        appender.setFile(logFileByMethod.getOrDefault(methodName, LOG_FILE_NAME_NO_METHOD));
        //appender.addFilter(filter);
        appender.start();
        return appender;
    }

    private Encoder<ILoggingEvent> getOrCreateEncoderByMethod(LoggerContext lc, @NotNull String method) {
        String pattern = patternByMethod.getOrDefault(method, DEFAULT_PATTERN);
        return getOrCreateEncoderByPattern(lc, pattern);
    }

    private Encoder<ILoggingEvent> getOrCreateEncoderByPattern(LoggerContext lc, @NotNull String pattern) {
        PatternLayoutEncoder encoder = encoderByPattern.get(pattern);
        if (encoder == null) {
            synchronized (encoderByPattern) {
                encoder = encoderByPattern.get(pattern);
                if (encoder == null) {
                    encoder = new PatternLayoutEncoder();
                    encoder.setContext(lc);
                    encoder.setPattern(pattern);
                    encoder.start();
                }
            }
        }
        return encoder;
    }
}
