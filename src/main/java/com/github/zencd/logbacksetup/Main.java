package com.github.zencd.logbacksetup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Example's main.
 */
public class Main {
    static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("===== program started; no method in context yet =====");

        CustomLogging.configureMethodsOneWay();
        CustomLogging.reconfigure();
        method1();
        method2();

        CustomLogging.configureMethodsAnotherWay();
        CustomLogging.reconfigure();
        method1();
        method2();
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

    static void method2() throws Exception {
        try {
            CustomLogging.setCurrentMethod("anotherMethod");
            log.debug("debug message 222");
            log.info("info message 222");
            log.warn("warn message 222");
            log.error("error message 222 {}", new Random().nextLong());

            runThreads();
        } finally {
            CustomLogging.unsetCurrentMethod();
        }
    }

    private static void runThreads() throws Exception {
        final Map<String, String> mdcOrig = MDC.getCopyOfContextMap();

        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(() -> ThreadWorker.run(mdcOrig));
        es.shutdown();
        es.awaitTermination(60, TimeUnit.SECONDS);
    }

    static class ThreadWorker {
        static final Logger log = LoggerFactory.getLogger(ThreadWorker.class);
        static void run(Map<String, String> parentMdc) {
            MDC.setContextMap(parentMdc);
            log.error("a message from thread worker, expected to appear in `method2*.log` {}", new Random().nextDouble());
        }
    }
}
