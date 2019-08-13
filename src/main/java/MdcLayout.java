import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

public class MdcLayout extends LayoutBase<ILoggingEvent> {

    private String appenderName;
    private String pattern;

    public String doLayout(ILoggingEvent event) {
        return Main.formatLogMessage(event, getContext(), appenderName, pattern);
    }

    /**
     * Used to configure an object via XML config.
     */
    public void setAppender(String appenderName) {
        this.appenderName = appenderName;
    }

    /**
     * Used to configure an object via XML config.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

}
