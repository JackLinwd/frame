package org.lwd.frame.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author lwd
 */
@Component("frame.util.logger")
public class LoggerImpl implements org.lwd.frame.util.Logger {
    private static final String[] THROWABLES = {"< ", " > ", "    "};

    protected Logger logger;
    protected boolean debugEnable;
    protected boolean infoEnable;

    public LoggerImpl() {
        logger = LogManager.getLogger("frame.util.logger");
        debugEnable = logger.isDebugEnabled();
        infoEnable = logger.isInfoEnabled();
    }

    @Override
    public boolean isDebugEnable() {
        return debugEnable;
    }

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message(null, message), args);
    }

    @Override
    public boolean isInfoEnable() {
        return infoEnable;
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message(null, message), args);
    }

    @Override
    public void warn(Throwable throwable, String message, Object... args) {
        logger.warn(message(throwable, message), args);
    }

    @Override
    public void error(Throwable throwable, String message, Object... args) {
        logger.error(message(throwable, message), args);
    }

    protected String message(Throwable throwable, String message) {
        StringBuilder sb = new StringBuilder().append(java.lang.Thread.currentThread().getStackTrace()[3].getClassName()).append(' ');
        append(sb, throwable);

        if (message != null)
            sb.append(message);

        return sb.toString();
    }

    protected void append(StringBuilder sb, Throwable throwable) {
        if (throwable == null)
            return;

        sb.append(THROWABLES[0]).append(throwable.getClass().getName()).append(THROWABLES[1]).append(throwable.getMessage()).append('\n');
        for (StackTraceElement stackTrace : throwable.getStackTrace())
            sb.append(THROWABLES[2]).append(stackTrace).append('\n');

        append(sb, throwable.getCause());
    }
}
