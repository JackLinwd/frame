package org.lwd.frame.ctrl.execute;

/**
 * 执行器支持。
 *
 * @author lwd
 */
public interface ExecutorHelper {
    /**
     * 设置当前线程正在执行的执行器。
     *
     * @param service 服务名称。
     */
    void set(String service);

    /**
     * 获取当前线程正在执行的执行器。
     *
     * @return 执行器。
     */
    Executor get();
}
