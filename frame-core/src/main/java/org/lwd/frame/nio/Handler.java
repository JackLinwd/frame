package org.lwd.frame.nio;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lwd.frame.util.Logger;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 监听处理器。
 *
 * @author lwd
 */
public abstract class Handler extends ChannelInboundHandlerAdapter {
    @Inject
    protected Logger logger;
    @Inject
    protected NioHelper nioHelper;
    private Map<String, ByteArrayOutputStream> outputStreamMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        String sessionId = nioHelper.getSessionId(context);
        ByteArrayOutputStream outputStream = outputStreamMap.get(sessionId);
        if (outputStream == null)
            outputStream = new ByteArrayOutputStream();
        byte[] bytes = nioHelper.read(message);
        outputStream.write(bytes, 0, bytes.length);
        outputStreamMap.put(sessionId, outputStream);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        String sessionId = nioHelper.getSessionId(context);
        ByteArrayOutputStream outputStream = outputStreamMap.get(sessionId);
        if (outputStream == null)
            return;

        outputStreamMap.remove(sessionId);
        outputStream.close();
        getListener().receive(sessionId, outputStream.toByteArray());
    }

    /**
     * 获取监听器。
     *
     * @return 监听器。
     */
    protected abstract Listener getListener();
}
