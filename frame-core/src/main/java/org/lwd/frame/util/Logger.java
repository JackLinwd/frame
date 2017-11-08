package org.lwd.frame.util;

/**
 * 日志管理器。
 *
 * @author lwd
 */
public interface Logger {
    /**
     * 判断是否允许输出DEBUF级别的日志信息。
     *
     * @return 如果允许则返回true；否则返回false。
     */
    boolean isDebugEnable();

    /**
     * 输出DEBUG级别的日志信息。
     *
     * @param message 日志信息。
     * @param args    参数集，依次替换掉字符串中的{}。
     */
    void debug(String message, Object... args);

    /**
     * 判断是否允许输出INFO级别的日志信息。
     *
     * @return 如果允许则返回true；否则返回false。
     */
    boolean isInfoEnable();

    /**
     * 输出INFO级别的日志信息。
     *
     * @param message 日志信息。
     * @param args    参数集，依次替换掉字符串中的{}。
     */
    void info(String message, Object... args);

    /**
     * 输出WARN级别的日志信息。
     *
     * @param throwable 异常信息，如果非异常信息可置为null。
     * @param message   日志信息。
     * @param args      参数集，依次替换掉字符串中的{}。
     */
    void warn(Throwable throwable, String message, Object... args);

    /**
     * 输出ERROR级别的日志信息。
     *
     * @param throwable 异常信息，如果非异常信息可置为null。
     * @param message   日志信息。
     * @param args      参数集，依次替换掉字符串中的{}。
     */
    void error(Throwable throwable, String message, Object... args);
}
