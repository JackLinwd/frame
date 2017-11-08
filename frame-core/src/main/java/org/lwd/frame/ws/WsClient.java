package org.lwd.frame.ws;

/**
 * WebSocket客户端。
 *
 * @author lwd
 */
public interface WsClient {
    /**
     * 连接远程WebSocket服务。
     *
     * @param listener 监听器。
     * @param url      远程WebSocket服务URL地址。
     */
    void connect(WsClientListener listener, String url);

    /**
     * 发送数据。
     *
     * @param message 数据。
     */
    void send(String message);

    /**
     * 关闭远程WebSocket连接。
     */
    void close();
}
