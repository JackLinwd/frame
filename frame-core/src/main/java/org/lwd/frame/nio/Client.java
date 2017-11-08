package org.lwd.frame.nio;

/**
 * 连接客户端。
 *
 * @author lwd
 */
public interface Client {
    /**
     * 连接服务端。
     *
     * @param listener 监听器。
     * @param ip       服务端IP地址。
     * @param port     服务端端口号。
     */
    void connect(ClientListener listener, String ip, int port);

    /**
     * 关闭连接。
     */
    void close();
}
