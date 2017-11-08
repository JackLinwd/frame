package org.lwd.frame.ctrl.http.ws;

import org.lwd.frame.atomic.Closable;
import org.lwd.frame.atomic.Failable;
import org.lwd.frame.bean.ContextClosedListener;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.crypto.Digest;
import org.lwd.frame.ctrl.http.IgnoreUri;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lwd
 */
@Service("frame.ctrl.http.ws.helper")
public class WsHelperImpl implements WsHelper, IgnoreUri, ContextRefreshedListener, ContextClosedListener {
    @Inject
    private Generator generator;
    @Inject
    private Digest digest;
    @Inject
    private Logger logger;
    @Inject
    private Set<Failable> failables;
    @Inject
    private Set<Closable> closables;
    @Inject
    protected Optional<WsListener> listener;
    @Value("${frame.ctrl.http.web-socket.max:64}")
    private int max;
    private AtomicLong counter;
    private Map<String, Session> sessions;
    private String key;

    @Override
    public void open(Session session) {
        if (!listener.isPresent() || counter.incrementAndGet() > max) {
            logger.warn(null, listener == null ? "未提供WsListener实现，无法开启WebSocket监听。" : "超过最大可连接数，拒绝新连接。");
            try {
                session.close();
            } catch (IOException e) {
                logger.warn(e, "关闭客户端Session时发生异常！");
            }

            return;
        }

        String key = getKey(session);
        sessions.put(key, session);
        listener.get().open(key);
        closables.forEach(Closable::close);
    }

    @Override
    public void message(Session session, String message) {
        if (!listener.isPresent())
            return;

        listener.get().message(getKey(session), message);
        closables.forEach(Closable::close);
    }

    @Override
    public void error(Session session, Throwable throwable) {
        if (!listener.isPresent())
            return;

        String key = getKey(session);
        listener.get().error(key, throwable);
        logger.warn(throwable, "WebSocket执行异常！");
        failables.forEach(failable -> failable.fail(throwable));
    }

    @Override
    public void close(Session session) {
        if (!listener.isPresent())
            return;

        String key = getKey(session);
        listener.get().close(key);
        sessions.remove(key);
        closables.forEach(Closable::close);
        counter.decrementAndGet();
    }

    private String getKey(Session session) {
        return digest.md5(key + session.getId());
    }

    @Override
    public void send(String sessionId, String message) {
        if (listener == null)
            return;

        Session session = sessions.get(sessionId);
        if (session == null) {
            logger.warn(null, "Session ID[{}]不存在！", sessionId);

            return;
        }

        send(session, message);
    }

    @Override
    public void send(String message) {
        if (listener == null)
            return;

        sessions.values().forEach(session -> send(session, message));
    }

    private void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.warn(e, "发送信息[{}]时发生异常！", message);
        }
    }

    @Override
    public void close(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            try {
                session.close();
            } catch (IOException e) {
                logger.warn(e, "关闭客户端Session[{}]时发生异常！", sessionId);
            }
        }
    }

    @Override
    public void close() {
        sessions.keySet().forEach(this::close);
    }

    @Override
    public String[] getIgnoreUris() {
        return new String[]{URI};
    }

    @Override
    public int getContextRefreshedSort() {
        return 18;
    }

    @Override
    public void onContextRefreshed() {
        counter = new AtomicLong();
        sessions = new ConcurrentHashMap<>();
        key = generator.random(32);
    }

    @Override
    public int getContextClosedSort() {
        return 18;
    }

    @Override
    public void onContextClosed() {
        close();
    }
}
