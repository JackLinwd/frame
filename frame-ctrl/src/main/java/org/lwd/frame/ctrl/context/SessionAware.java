package org.lwd.frame.ctrl.context;

/**
 * Session参数注入器。用于设置Session参数。
 *
 * @author lwd
 */
public interface SessionAware {
    /**
     * 设置Session适配器。
     *
     * @param adapter Session适配器。
     */
    void set(SessionAdapter adapter);
}
