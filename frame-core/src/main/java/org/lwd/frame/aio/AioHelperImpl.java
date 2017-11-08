package org.lwd.frame.aio;

import org.lwd.frame.scheduler.MinuteJob;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Thread;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lwd
 */
@Component("frame.aio.helper")
public class AioHelperImpl implements AioHelper, MinuteJob {
    private static final int BUFFER_SZE = 4 * (1 << 10); // 4K

    @Inject
    private Thread thread;
    @Inject
    private Logger logger;
    private Map<String, AsynchronousSocketChannel> map = new ConcurrentHashMap<>();
    private Map<String, ByteBuffer> buffers = new ConcurrentHashMap<>();
    private Map<String, ExecutorService> services = new ConcurrentHashMap<>();

    @Override
    public String put(AsynchronousSocketChannel socketChannel) {
        String sessionId = getSessionId(socketChannel);
        map.put(sessionId, socketChannel);

        return sessionId;
    }

    @Override
    public String getSessionId(AsynchronousSocketChannel socketChannel) {
        return socketChannel.toString();
    }

    @Override
    public ByteBuffer getBuffer(String sessionId) {
        return buffers.computeIfAbsent(sessionId, sid -> ByteBuffer.allocate(BUFFER_SZE));
    }

    @Override
    public void send(String sessionId, byte[] message) {
        if (!map.containsKey(sessionId) || !map.get(sessionId).isOpen())
            return;

        services.computeIfAbsent(sessionId, sid -> Executors.newFixedThreadPool(1))
                .submit(() -> map.get(sessionId).write(ByteBuffer.wrap(message)));
    }

    @Override
    public void close(String sessionId) {
        if (!map.containsKey(sessionId))
            return;

        try {
            map.remove(sessionId).close();
            buffers.remove(sessionId);
            services.remove(sessionId).shutdown();
        } catch (IOException e) {
            logger.warn(e, "关闭AIO Socket Channel[{}]时发生异常！", sessionId);
        }
    }

    @Override
    public void executeMinuteJob() {
        Set<String> set = new HashSet<>();
        map.forEach((sessionId, socketChannel) -> {
            if (socketChannel.isOpen())
                return;

            set.add(sessionId);
        });
        set.forEach(this::close);
    }
}
