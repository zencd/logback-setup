package com.github.zencd.logbacksetup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.Random;

public class Main {
    static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        RuntimeLogging.forceConfigure(); // force loading of class RuntimeLogging
        log.info("===== program started; no method in context yet =====");
        method1();
        method2();
    }

    static void method1() {
        try {
            MDC.put(RuntimeLogging.MDC_KEY_METHOD, "someMethod");
            log.debug("debug message");
            log.info("info message");
            log.warn("warn message");
            log.error("error message {}", new Random().nextLong());
        } finally {
            MDC.remove(RuntimeLogging.MDC_KEY_METHOD);
        }
    }

    static void method2() {
        try {
            MDC.put(RuntimeLogging.MDC_KEY_METHOD, "anotherMethod");
            log.debug("debug message 222");
            log.info("info message 222");
            log.warn("warn message 222");
            log.error("error message 222 {}", new Random().nextLong());
        } finally {
            MDC.remove(RuntimeLogging.MDC_KEY_METHOD);
        }
    }
}
