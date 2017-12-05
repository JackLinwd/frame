package org.lwd.frame.dao.test;

import org.lwd.frame.ctrl.context.SessionAdapter;

/**
 * @author lwd
 */
public interface MockSession extends SessionAdapter {
    /**
     * 设置Session ID值。
     *
     * @param id Session ID值。
     */
    void setId(String id);
}
