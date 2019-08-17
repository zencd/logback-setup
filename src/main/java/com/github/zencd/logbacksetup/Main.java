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

        CustomLogging.readConfig1();
        CustomLogging.reconfigure();
        callMethod("method1", Main::method1);
        //callMethod("method2", Main::method2);

        CustomLogging.readConfig2();
        CustomLogging.reconfigure();
        callMethod("method1", Main::method1);
        //callMethod("method2", Main::method2);
    }

    private static void callMethod(String methodName, Runnable runnable) {
        try {
            CustomLogging.startMethod(methodName);
            runnable.run();
        } finally {
            CustomLogging.endMethod();
        }
    }

    static void method1() {
        log.debug("M1: debug message");
        log.info("M1: info message");
        log.warn("M1: warn message");
        log.error("M1: error message {}", new Random().nextLong());

        callMethod("method2", Main::method2);

        log.error("M1: control returned");
    }

    static void method2() {
        log.debug("M2: debug message 222");
        log.info("M2: info message 222");
        log.warn("M2: warn message 222");
        log.error("M2: error message 222 {}", new Random().nextLong());

        AnotherClass.run();

        runThreads();
    }

    private static void runThreads() {
        final Map<String, String> mdcOrig = MDC.getCopyOfContextMap();

        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(() -> ThreadWorker.run(mdcOrig));
        es.shutdown();
        try {
            es.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class ThreadWorker {
        static final Logger log = LoggerFactory.getLogger(ThreadWorker.class);
        static void run(Map<String, String> parentMdc) {
            MDC.setContextMap(parentMdc);
            log.error("a message from thread worker, expected to appear in `method2*.log` {}", new Random().nextDouble());
        }
    }

    static class AnotherClass {
        static final Logger log = LoggerFactory.getLogger(AnotherClass.class);
        static void run() {
            log.error("expected to appear in method2*.log");
        }
    }
}
