package org.lwd.frame.ctrl;

import java.util.concurrent.Callable;

/**
 * 处理队列。
 *
 * @author lwd
 */
public interface Handler {
    /**
     * 添加到处理队列。
     *
     * @param key      队列KEY。
     * @param callable 处理逻辑。
     * @param <T>      返回值类型。
     * @return 返回数据。
     * @throws Exception 执行异常。
     */
    <T> T call(String key, Callable<T> callable) throws Exception;

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
