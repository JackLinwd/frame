package org.lwd.frame.test;

/**
 * 定时器切片。主要实现对定时器执行的控制。
 *
 * @author lwd
 */
public interface SchedulerAspect {
    /**
     * 暂停定时器。
     */
    void pause();

    /**
     * 继续定时器。
     */
    void press();
}
