package org.lwd.frame.ctrl.context;

import java.util.Map;

/**
 * 请求头适配器。
 *
 * @author lwd
 */
public interface HeaderAdapter {
    /**
     * 获得请求头信息值。
     *
     * @param name 头信息名称。
     * @return 头信息值。
     */
    String get(String name);

    /**
     * 获得请求者IP地址。
     *
     * @return 请求者IP地址。
     */
    String getIp();

    /**
     * 获得所有请求头信息值对。
     *
     * @return 请求头信息值对。
     */
    Map<String, String> getMap();
}
