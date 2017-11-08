package org.lwd.frame.nio;

/**
 * 服务端监听器。用于处理来自客户端的消息。
 *
 * @author lwd
 */
public interface ServerListener extends Listener {
    /**
     * 获得监听端口号。
     *
     * @return 端口号。
     */
    int getPort();

    /**
     * 获得最大处理线程数。
     *
     * @return 最大处理线程数。
     */
    int getMaxThread();

    /**
     * 接受新连接。当监听端口完成新Socket连接时，通知此方法。
     *
     * @param sessionId 客户端Session ID。
     */
    void accept(String sessionId);

    /**
     * 连接断开。当连接断开时回调此方法。
     *
     * @param sessionId 客户端Session ID。
     */
    void disconnect(String sessionId);
}
