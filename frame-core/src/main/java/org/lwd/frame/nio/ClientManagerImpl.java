package org.lwd.frame.nio;

import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextClosedListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lwd
 */
@Component("frame.nio.client-manager")
public class ClientManagerImpl implements ClientManager, ContextClosedListener {
    private Set<Client> clients = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Client get() {
        Client client = BeanFactory.getBean(Client.class);
        clients.add(client);

        return client;
    }

    @Override
    public void close() {
        if (clients.isEmpty())
            return;

        clients.forEach(Client::close);
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
