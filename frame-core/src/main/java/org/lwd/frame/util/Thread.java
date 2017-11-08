package org.lwd.frame.util;

/**
 * 提供简化的线程操作工具集。
 *
 * @author lwd
 */
public interface Thread {
    /**
     * 休眠。
     *
     * @param time 休眠时间。
     * @param unit 时间单位。
     */
    void sleep(int time, TimeUnit unit);

    /**
     * 休眠，时间按指定范围[min,max]随机。
     *
     * @param min  随机数最小值。
     * @param max  随机数最大值。
     * @param unit 时间单位。
     */
    void sleep(int min, int max, TimeUnit unit);
}
