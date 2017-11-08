package org.lwd.frame.nio;

/**
 * 监听器，用于处理接收的数据。
 *
 * @author lwd
 */
public interface Listener {
    /**
     * 接收数据。当监听端口接收到数据时，通知此方法。
     *
     * @param sessionId 客户端Session ID值。
     * @param message   数据。
     */
    void receive(String sessionId, byte[] message);
}
