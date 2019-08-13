package com.github.zencd.logbacksetup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Random;

/**
 * Example's main.
 */
public class Main {
    static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("===== program started; no method in context yet =====");

        CustomLogging.configureMethodsOneWay();
        CustomLogging.reconfigure();
        method1();
        method2();

        CustomLogging.configureMethodsAnotherWay();
        CustomLogging.reconfigure();
        method1();
        method2();

        Service.service();
    }

    static void method1() {
        try {
            CustomLogging.setCurrentMethod("someMethod");
            log.debug("debug message");
            log.info("info message");
            log.warn("warn message");
            log.error("error message {}", new Random().nextLong());
        } finally {
            CustomLogging.unsetCurrentMethod();
        }
    }

    static void method2() {
        try {
            CustomLogging.setCurrentMethod("anotherMethod");
            log.debug("debug message 222");
            log.info("info message 222");
            log.warn("warn message 222");
            log.error("error message 222 {}", new Random().nextLong());
        } finally {
            CustomLogging.unsetCurrentMethod();
        }
    }

    static class Service {
        static final Logger log = LoggerFactory.getLogger(Service.class);
        static void service() {
            log.info("a message from another class");
        }
    }
}
