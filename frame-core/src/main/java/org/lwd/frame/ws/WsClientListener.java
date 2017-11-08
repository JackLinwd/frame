package org.lwd.frame.ws;

/**
 * @author lwd
 */
public interface WsClientListener {
    /**
     * 连接完成。当连接完成时回调此方法。
     */
    void connect();

    /**
     * 判断是否为最后一个数据包。
     *
     * @param message 数据包。
     * @return 如果是则返回true；否则返回false。
     */
    boolean complete(byte[] message);

    /**
     * 接收数据。当监听端口接收到数据时，通知此方法。
     *
     * @param message 数据。
     */
    void receive(String message);

    /**
     * 连接被断开。当连接被断开时回调此方法。
     */
    void disconnect();
}
