package org.lwd.frame.cache.lr;

import org.lwd.frame.nio.NioHelper;
import org.lwd.frame.nio.ServerListener;
import org.lwd.frame.util.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Component("frame.cache.lr.listener")
public class ListenerImpl implements ServerListener {
    @Inject
    private Serializer serializer;
    @Inject
    private NioHelper nioHelper;
    @Inject
    private Local local;
    @Inject
    private Remote remote;
    @Value("${frame.cache.remote.port:0}")
    private int port;
    @Value("${frame.cache.remote.thread:5}")
    private int thread;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getMaxThread() {
        return thread;
    }

    @Override
    public void accept(String sessionId) {
        nioHelper.send(sessionId, remote.getId().getBytes());
    }

    @Override
    public void receive(String sessionId, byte[] message) {
        if (message == null)
            return;

        Object object = serializer.unserialize(message);
        if (object == null)
            return;

        if (object instanceof String)
            local.remove((String) object);
        else if (object instanceof Element)
            local.put((Element) object);
    }

    @Override
    public void disconnect(String sessionId) {
    }
}
