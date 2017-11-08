package org.lwd.frame.ws;

import org.lwd.frame.aio.AioClient;
import org.lwd.frame.aio.AioClientListener;
import org.lwd.frame.aio.AioClients;
import org.lwd.frame.aio.AioHelper;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

/**
 * @author lwd
 */
@Component("frame.ws.client")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class WsClientImpl implements WsClient, AioClientListener {
    private static final byte[] HANDSHAKE = "HTTP/1.1 101".getBytes();
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private AioHelper aioHelper;
    @Inject
    private AioClients aioClients;
    private AioClient aioClient;
    private WsClientListener listener;
    private URI uri;
    private String sessionId;
    private ByteArrayOutputStream outputStream;

    @Override
    public void connect(WsClientListener listener, String url) {
        this.listener = listener;
        try {
            uri = new URI(url);
            aioClient = aioClients.get();
            aioClient.connect(uri.getHost(), uri.getPort(), this);
        } catch (URISyntaxException e) {
            logger.warn(e, "解析URL[{}]地址时发生异常！", url);
        }
    }

    @Override
    public void send(String message) {
        byte[] msg = message.getBytes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(0x80 | 1);
        writeLength(outputStream, msg.length);
        byte[] mask = new byte[4];
        for (int i = 0; i < mask.length; i++)
            mask[i] = (byte) generator.random(Byte.MIN_VALUE, Byte.MAX_VALUE);
        outputStream.write(mask, 0, mask.length);
        for (int i = 0; i < msg.length; i++)
            outputStream.write((msg[i] ^ mask[i % 4]) & 0xff);
        try {
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "关闭输出流时发生异常！");
        }
        aioHelper.send(sessionId, outputStream.toByteArray());
        if (logger.isDebugEnable())
            logger.debug("发送数据[{}]到WebSocket服务[{}]。", message, uri.toString());
    }

    private void writeLength(ByteArrayOutputStream outputStream, int length) {
        if (length <= 125)
            outputStream.write(0x80 | length);
        else if (length <= 0xffff) {
            outputStream.write(0x80 | 126);
            outputStream.write((length >> 8) & 0xff);
            outputStream.write(length & 0xff);
        } else {
            outputStream.write(0x80 | 127);
            for (int i = 7; i >= 0; i--)
                outputStream.write((length >> (i * 8)) & 0xff);
        }
    }

    @Override
    public void connect(String sessionId) {
        this.sessionId = sessionId;
        StringBuilder sb = new StringBuilder().append("GET ").append(uri.getPath()).append(" HTTP/1.1\r\n")
                .append("Host: ").append(uri.getHost()).append(':').append(uri.getPort()).append("\r\n")
                .append("Upgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Protocol: chat, superchat\r\nSec-WebSocket-Version: 13\r\n")
                .append("Sec-WebSocket-Key: ").append(Base64.getUrlEncoder().encodeToString(generator.random(32).getBytes())).append("\r\n")
                .append("Origin: http://").append(uri.getHost()).append(':').append(uri.getPort()).append("\r\n\r\n");
        if (logger.isDebugEnable())
            logger.debug("发送连接Handshake[{}]到WebSocket服务[{}]。", sb.toString(), uri.toString());
        aioHelper.send(sessionId, sb.toString().getBytes());
    }

    @Override
    public void receive(String sessionId, byte[] message) {
        if (isHandshake(message)) {
            if (logger.isDebugEnable())
                logger.debug("接收到WebSocket[{}]推送的Handshake[{}]。", uri.toString(), new String(message));
            listener.connect();

            return;
        }

        if (outputStream == null)
            outputStream = new ByteArrayOutputStream();
        outputStream.write(message, 0, message.length);
        if (listener.complete(message)) {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.warn(e, "关闭输出流时发生异常！");
            }
            receive(outputStream.toByteArray());
            outputStream = null;
        }
    }

    private boolean isHandshake(byte[] message) {
        if (message.length < HANDSHAKE.length)
            return false;

        for (int i = 0; i < HANDSHAKE.length; i++)
            if (message[i] != HANDSHAKE[i])
                return false;

        return true;
    }

    private void receive(byte[] message) {
        if (logger.isDebugEnable())
            logger.debug("接收到WebSocket[{}]服务推送的数据[{}]。", uri.toString(), converter.toBitSize(message.length));
        long length = message[1] & 0x7f;
        int start = 2;
        if (length == 126)
            start += 2;
        else if (length == 127)
            start += 8;
        boolean mask = (message[1] & 0x80) == 0x80;
        byte[] masks = new byte[4];
        if (mask) {
            System.arraycopy(masks, start, masks, 0, masks.length);
            start += 4;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (int i = start, j = 0; i < message.length; i++)
                outputStream.write(mask ? unmask(message[i], masks[j++ % masks.length]) : message[i]);
            outputStream.close();
            listener.receive(outputStream.toString());
        } catch (IOException e) {
            logger.warn(e, "关闭输出流时发生异常！");
        }
    }

    private int unmask(byte message, byte mask) {
        int um = message ^ mask;
        if ((um & 0x80) == 0x80)
            um |= 0xffffff00;

        return um;
    }

    @Override
    public void disconnect(String sessionId) {
    }

    @Override
    public void close() {
        aioHelper.send(sessionId, new byte[]{(byte) (0x80 | 0x8)});
        aioClient.close();
    }
}
