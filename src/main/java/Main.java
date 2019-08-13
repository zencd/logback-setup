import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

// todo use https://logback.qos.ch/manual/encoders.html
// todo use https://logback.qos.ch/manual/encoders.html
// todo use https://logback.qos.ch/manual/encoders.html

@interface Logging {
    String component();
    String service();
    String method();
}

public class Main {
    public static void main(String[] args) throws JoranException {
        configureLogback("logback-config-01.xml");

        MDC.put("method", "method1");

        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");

        MDC.remove("method");
    }

    public static String formatLogMessage(ILoggingEvent event, Context context, String appenderName, String defaultPattern) {
        // todo probably we need allocate formatters at the stage of config parsing and then we may omit try/catch here
        // xxx using a try/catch for every log message looks like a significant overhead
        //String pattern = null;
        //pattern = findPattern(appenderName, defaultPattern);
        //FormatContext fc = new FormatContext();
        //return getFormatter(pattern, context).format(event, fc);
        return "" + MDC.get("method") + ": " + event.getFormattedMessage() + "\n";
    }

    private String formatPoorly(ILoggingEvent event, Context context, String pattern) {
        PatternLayout layoutImpl = new PatternLayout();
        layoutImpl.setPattern(pattern);
        layoutImpl.setContext(context);
        layoutImpl.start();
        String s = layoutImpl.doLayout(event);
        layoutImpl.stop();
        return s;
    }

    static void configureLogback(String xmlFileName) throws JoranException {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset(); //  reset prev config
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        configurator.doConfigure(xmlFileName);
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

        ch.qos.logback.classic.Logger root = lc.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO); // override level in XML
    }

}
