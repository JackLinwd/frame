package org.lwd.frame.scheduler;

import java.util.Date;

/**
 * 定时任务支持。
 *
 * @author lwd
 */
public interface SchedulerHelper {
    /**
     * 延迟执行任务。
     *
     * @param job  任务。
     * @param time 延迟时间，单位：毫秒。
     */
    void delay(SchedulerJob job, long time);

    /**
     * 指定时间执行任务。
     *
     * @param job  任务。
     * @param time 执行时间。
     */
    void at(SchedulerJob job, Date time);
}
