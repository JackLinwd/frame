package org.lwd.frame.cache.lr;

/**
 * 远程缓存客户端通道。
 *
 * @author lwd
 */
public interface Channel {
    enum State {
        Disconnect, Connected, Self
    }

    /**
     * 设置远程缓存服务端IP地址。
     *
     * @param ip   远程缓存服务端IP地址。
     * @param port 设置远程缓存服务端口号。
     */
    void set(String ip, int port);

    /**
     * 建立连接。
     */
    void connect();

    /**
     * 获取连接Session ID值。
     *
     * @return 连接Session ID值。
     */
    String getSessionId();

    /**
     * 获取连接状态。
     *
     * @return 连接状态。
     */
    State getState();
}
