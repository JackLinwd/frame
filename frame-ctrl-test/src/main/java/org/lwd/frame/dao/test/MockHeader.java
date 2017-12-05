package org.lwd.frame.dao.test;

import org.lwd.frame.ctrl.context.HeaderAdapter;

import java.util.Map;

/**
 * @author lwd
 */
public interface MockHeader extends HeaderAdapter {
    /**
     * 设置IP地址。
     *
     * @param ip IP地址。
     */
    void setIp(String ip);

    /**
     * 添加头信息。
     *
     * @param name  名称。
     * @param value 值。
     */
    void addHeader(String name, String value);

    /**
     * 设置请求头集。
     *
     * @param map 请求头集。
     */
    void setMap(Map<String, String> map);
}
