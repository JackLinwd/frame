package org.lwd.frame.aio;

import org.lwd.frame.scheduler.SchedulerHelper;
import org.lwd.frame.util.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author lwd
 */
@Component("frame.aio.handler.receive")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ReceiveHandlerImpl implements ReceiveHandler {
    @Inject
    private SchedulerHelper schedulerHelper;
    @Inject
    private Logger logger;
    @Inject
    private AioHelper aioHelper;
    private AsynchronousSocketChannel socketChannel;
    private ByteBuffer buffer;
    private String sessionId;
    private AioListener listener;

    @Override
    public ReceiveHandler bind(AsynchronousSocketChannel socketChannel, ByteBuffer buffer, String sessionId, AioListener listener) {
        this.socketChannel = socketChannel;
        this.buffer = buffer;
        this.sessionId = sessionId;
        this.listener = listener;

        return this;
    }

    @Override
    public void completed(Integer integer, Object object) {
        if (integer == -1) {
            if (logger.isDebugEnable())
                logger.debug("AIO连接[{}]断开。", socketChannel);

            listener.disconnect(sessionId);
            aioHelper.close(sessionId);

            return;
        }

        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        buffer.clear();
        listener.receive(sessionId, bytes);
        socketChannel.read(buffer, null, this);
    }

    @Override
    public void failed(Throwable throwable, Object object) {
        if (socketChannel.isOpen())
            logger.warn(throwable, "监听AIO[{}]数据时发生异常！", socketChannel);
    }
}
