package org.lwd.frame.ctrl.http;

/**
 * 忽略URI地址。
 *
 * @author lwd
 */
public interface IgnoreUri {
    /**
     * 获取忽略的URI地址集。
     *
     * @return URI地址集。
     */
    String[] getIgnoreUris();
}
