package org.lwd.frame.scheduler;

/**
 * 每小时执行定时器任务，每小时执行一次任务。
 *
 * @author lwd
 */
public interface HourJob {
    /**
     * 执行每小时任务。
     */
    void executeHourJob();
}
