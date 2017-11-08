package org.lwd.frame.aio;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author lwd
 */
@Component("frame.aio.client")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AioClientImpl extends HandlerSupport implements AioClient, CompletionHandler<Void, Object> {
    private String host;
    private AioClientListener listener;
    private AsynchronousSocketChannel socketChannel;
    private String sessionId;

    @Override
    public void connect(String host, int port, AioClientListener listener) {
        this.host = host;
        this.port = port;
        this.listener = listener;
        try {
            socketChannel = AsynchronousSocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port), null, this);
        } catch (IOException e) {
            logger.warn(e, "连接到服务端[{}:{}]时发生异常！", host, port);
        }
    }

    @Override
    public void completed(Void aVoid, Object object) {
        sessionId = aioHelper.put(socketChannel);
        listener.connect(sessionId);
        if (logger.isDebugEnable())
            logger.debug("连接到远程服务[{}:{}:{}]。", host, port, sessionId);
        read(socketChannel, sessionId, listener);
    }

    @Override
    public void failed(Throwable throwable, Object object) {
        logger.warn(throwable, "AIO连接[{}:{}:{}]异常！", host, port, sessionId);
    }

    @Override
    public void close() {
        listener.disconnect(sessionId);
        aioHelper.close(sessionId);
    }
}
