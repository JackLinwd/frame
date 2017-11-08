package org.lwd.frame.ctrl.context;

import java.util.Map;

/**
 * @author lwd
 */
public interface Header {
    /**
     * 获得请求头信息值。
     *
     * @param name 头信息名称。
     * @return 头信息值。
     */
    String get(String name);

    /**
     * 获得整型请求头信息值。
     *
     * @param name 头信息名称。
     * @return 整型头信息值。
     */
    int getAsInt(String name);

    /**
     * 获得整型请求头信息值。
     *
     * @param name 头信息名称。
     * @return 整型头信息值。
     */
    long getAsLong(String name);

    /**
     * 获得请求者IP地址。
     *
     * @return 请求者IP地址。
     */
    String getIp();

    /**
     * 设置求者IP地址。
     *
     * @param ip 求者IP地址。
     */
    void setIp(String ip);

    /**
     * 获得所有请求头信息值对。
     *
     * @return 请求头信息值对。
     */
    Map<String, String> getMap();
}
