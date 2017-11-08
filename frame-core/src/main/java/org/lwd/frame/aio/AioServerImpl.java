package org.lwd.frame.aio;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

/**
 * @author lwd
 */
@Component("frame.aio.server")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AioServerImpl extends HandlerSupport implements AioServer, CompletionHandler<AsynchronousSocketChannel, Object> {
    private AioServerListener listener;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousServerSocketChannel serverSocketChannel;

    @Override
    public void listen(int thread, int port, AioServerListener listener) {
        this.port = port;
        this.listener = listener;
        try {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(thread, Executors.defaultThreadFactory());
            serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
            serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.accept(null, this);

            if (logger.isInfoEnable())
                logger.info("启动AIO监听[{}]服务。", port);
        } catch (IOException e) {
            logger.warn(e, "启动AIO监听[{}]服务时发生异常！", port);
        }
    }

    @Override
    public void completed(AsynchronousSocketChannel socketChannel, Object object) {
        serverSocketChannel.accept(null, this);

        String sessionId = aioHelper.put(socketChannel);
        if (logger.isDebugEnable())
            logger.debug("收到AIO[{}]连接。", sessionId);
        listener.accept(sessionId);
        read(socketChannel, sessionId, listener);
    }

    @Override
    public void failed(Throwable throwable, Object object) {
        logger.warn(throwable, "AIO服务[{}]异常！", port);
    }

    @Override
    public void close() {
        try {
            serverSocketChannel.close();
            channelGroup.shutdown();
        } catch (IOException e) {
            logger.warn(e, "关闭AIO监听[{}]服务时发生异常！", port);
        }
    }
}
