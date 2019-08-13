package com.github.zencd.logbacksetup;

import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Main {
    public static void main(String[] args) throws JoranException {
        RuntimeLogging.INSTANCE.configure();

        MDC.put(RuntimeLogging.MDC_KEY_METHOD, "method1");
        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");
        MDC.remove(RuntimeLogging.MDC_KEY_METHOD);

        MDC.put(RuntimeLogging.MDC_KEY_METHOD, "method2");
        logger.debug("debug message 222");
        logger.info("info message 222");
        logger.warn("warn message 222");
        logger.error("error message 222");
        MDC.remove(RuntimeLogging.MDC_KEY_METHOD);
    }
}
