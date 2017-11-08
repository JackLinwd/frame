package org.lwd.frame.nio;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author lwd
 */
public interface NioHelper {
    /**
     * 保存ChannelHandlerContext对象。
     *
     * @param context ChannelHandlerContext实例。
     * @return Session ID值。
     */
    String put(ChannelHandlerContext context);

    /**
     * 获取Session ID值。
     *
     * @param context ChannelHandlerContext实例。
     * @return Session ID值。
     */
    String getSessionId(ChannelHandlerContext context);

    /**
     * 获取请求方IP地址。
     *
     * @param sessionId Session ID值。
     * @return 请求方IP地址。
     */
    String getIp(String sessionId);

    /**
     * 读取数据。
     *
     * @param message 数据源。
     * @return 数据。
     */
    byte[] read(Object message);

    /**
     * 发送信息。
     *
     * @param sessionId Session ID值。
     * @param message   信息。
     */
    void send(String sessionId, byte[] message);

    /**
     * 关闭连接。
     *
     * @param sessionId Session ID值。
     */
    void close(String sessionId);
}
