package org.lwd.frame.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 接收处理器。
 *
 * @author lwd
 */
public interface ReceiveHandler extends CompletionHandler<Integer, Object> {
    /**
     * 绑定。
     *
     * @param socketChannel 通道。
     * @param buffer        缓冲区。
     * @param sessionId     Session ID。
     * @param listener      监听器。
     * @return 当前接收处理器。
     */
    ReceiveHandler bind(AsynchronousSocketChannel socketChannel, ByteBuffer buffer, String sessionId, AioListener listener);
}
