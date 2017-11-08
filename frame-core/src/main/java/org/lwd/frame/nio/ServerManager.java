package org.lwd.frame.nio;

/**
 * 监听器管理器。负责监听器的创建与关闭。
 *
 * @author lwd
 */
public interface ServerManager {
    /**
     * 关闭监听器。
     */
    void stop();
}
