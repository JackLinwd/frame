package org.lwd.frame.ws;

/**
 * WebSocket客户端管理器。
 *
 * @author lwd
 */
public interface WsClients {
    /**
     * 获取一个新的WebSocket客户端连接实例。
     *
     * @return 新的WebSocket客户端连接实例。
     */
    WsClient get();

    /**
     * 关闭WebSocket客户端连接。
     */
    void close();
}
