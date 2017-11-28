package org.lwd.frame.ctrl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 处理队列。
 *
 * @author lwd
 */
public interface Handler {
    /**
     * 添加到处理队列，并等待执行结果。
     *
     * @param key      队列KEY。
     * @param callable 处理逻辑。
     * @param <T>      返回值类型。
     * @return 返回数据类型。
     * @throws Exception 执行异常。
     */
    <T> T call(String key, Callable<T> callable) throws Exception;

    /**
     * 添加到处理队列。
     *
     * @param key      队列KEY。
     * @param callable 处理逻辑。
     * @param <T>      返回值类型。
     * @return 执行任务；如果未启用队列则返回null。
     */
    <T> Future<T> submit(String key, Callable<T> callable);

    /**
     * 添加到处理队列。
     *
     * @param key      队列KEY。
     * @param runnable 处理逻辑。
     */
    void run(String key, Runnable runnable);

    /**
     * 清除队列。
     *
     * @param key 队列KEY。
     */
    void clear(String key);
}
