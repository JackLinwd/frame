package org.lwd.frame.ctrl.context;

import java.util.Map;

/**
 * 请求适配器。
 *
 * @author lwd
 */
public interface RequestAdapter {
    /**
     * 获得请求参数值。
     *
     * @param name 参数名称。
     * @return 参数值。
     */
    String get(String name);

    /**
     * 获取请求参数值数组。
     *
     * @param name 参数名称。
     * @return 参数值。
     */
    String[] getAsArray(String name);

    /**
     * 获得所有请求参数值对。
     *
     * @return 请求参数值对。
     */
    Map<String, String> getMap();

    /**
     * 获取InputStream中的数据。
     *
     * @return InputStream中的数据；如果不存在则返回空字符串。
     */
    String getFromInputStream();

    /**
     * 获取服务器名。
     *
     * @return 服务器名。
     */
    String getServerName();

    /**
     * 获取服务器端口号。
     *
     * @return 服务器端口号。
     */
    int getServerPort();

    /**
     * 获取部署项目名。
     *
     * @return 部署项目名。
     */
    String getContextPath();

    /**
     * 获取请求URL。
     *
     * @return 请求URL。
     */
    String getUrl();

    /**
     * 获取请求URI。
     *
     * @return 请求URI。
     */
    String getUri();

    /**
     * 获取请求方法。
     *
     * @return 请求方法。
     */
    String getMethod();
}
