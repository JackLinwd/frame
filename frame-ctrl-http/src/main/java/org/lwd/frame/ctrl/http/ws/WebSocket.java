package org.lwd.frame.ctrl.http.ws;

import org.lwd.frame.bean.BeanFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * @author lwd
 */
@ServerEndpoint(WsHelper.URI)
public class WebSocket {
    protected WsHelper wsHelper;

    public WebSocket() {
        wsHelper = BeanFactory.getBean(WsHelper.class);
    }

    @OnOpen
    public void open(Session session) {
        wsHelper.open(session);
    }

    @OnMessage
    public void message(Session session, String message) {
        wsHelper.message(session, message);
    }

    @OnError
    public void error(Session session, Throwable throwable) {
        wsHelper.error(session, throwable);
    }

    @OnClose
    public void close(Session session) {
        wsHelper.close(session);
    }
}
