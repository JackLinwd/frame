package org.lwd.frame.ctrl.http.context;

import org.lwd.frame.ctrl.context.SessionAdapter;

/**
 * @author lwd
 */
public class SessionAdapterImpl implements SessionAdapter {
    protected String sessionId;

    public SessionAdapterImpl(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getId() {
        return sessionId;
    }
}
