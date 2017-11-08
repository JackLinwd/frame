package org.lwd.frame.util;

/**
 * 时间单位。
 *
 * @author lwd
 */
public enum TimeUnit {
    /**
     * 毫秒。
     */
    MilliSecond(1L),
    /**
     * 秒。
     */
    Second(1000L),
    /**
     * 分钟。
     */
    Minute(60L * 1000),
    /**
     * 小时。
     */
    Hour(60L * 60 * 1000),
    /**
     * 天。
     */
    Day(24L * 60 * 60 * 1000);

    long time;

    TimeUnit(long time) {
        this.time = time;
    }

    /**
     * 获取时间值。
     *
     * @return 时间值。
     */
    public long getTime() {
        return time;
    }
}
