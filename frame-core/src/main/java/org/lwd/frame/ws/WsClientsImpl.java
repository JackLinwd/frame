package org.lwd.frame.ws;

import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextClosedListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lwd
 */
@Component("frame.ws.clients")
public class WsClientsImpl implements WsClients, ContextClosedListener {
    private Set<WsClient> clients = Collections.synchronizedSet(new HashSet<>());

    @Override
    public WsClient get() {
        WsClient client = BeanFactory.getBean(WsClient.class);
        clients.add(client);

        return client;
    }

    @Override
    public void close() {
        if (clients.isEmpty())
            return;

        clients.forEach(WsClient::close);
    }

    @Override
    public int getContextClosedSort() {
        return 5;
    }

    @Override
    public void onContextClosed() {
        close();
    }
}
