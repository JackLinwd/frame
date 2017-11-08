package org.lwd.frame.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * AIO支持。
 *
 * @author lwd
 */
public interface AioHelper {
    /**
     * 保存通道。
     *
     * @param socketChannel 通道实例。
     * @return Session ID值。
     */
    String put(AsynchronousSocketChannel socketChannel);

    /**
     * 获取Session ID值。
     *
     * @param socketChannel 通道实例。
     * @return Session ID值。
     */
    String getSessionId(AsynchronousSocketChannel socketChannel);

    /**
     * 获取缓冲区。
     *
     * @param sessionId Session ID值。
     * @return 缓冲区。
     */
    ByteBuffer getBuffer(String sessionId);

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
