package org.lwd.frame.nio;

/**
 * 服务器。用于管理端口监听。
 *
 * @author lwd
 */
public interface Server {
    /**
     * 启动服务。
     *
     * @param listener 监听器。
     */
    void listen(ServerListener listener);

    /**
     * 关闭服务。
     */
    void close();
}
