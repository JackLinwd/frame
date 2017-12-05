package org.lwd.frame.dao.test;

import org.lwd.frame.ctrl.context.RequestAdapter;

import java.util.Map;

/**
 * @author lwd
 */
public interface MockRequest extends RequestAdapter {
    /**
     * 设置服务器名称。
     *
     * @param serverName 服务器名称。
     */
    void setServerName(String serverName);

    /**
     * 设置服务段口号。
     *
     * @param serverPort 服务段口号。
     */
    void setServerPort(int serverPort);

    /**
     * 设置部署项目名。
     *
     * @param contextPath 部署项目名。
     */
    void setContextPath(String contextPath);

    /**
     * 设置请求方式。
     *
     * @param method 请求方式。
     */
    void setMethod(String method);

    /**
     * 设置请求URL地址。
     *
     * @param url 请求URL地址。
     */
    void setUrl(String url);

    /**
     * 设置请求URI地址。
     *
     * @param uri 请求URI地址。
     */
    void setUri(String uri);

    /**
     * 设置请求参数集。
     *
     * @param parameters 请求参数集。
     */
    void setParameters(Map<String, String> parameters);

    /**
     * 添加参数。
     *
     * @param name  参数名称。
     * @param value 参数值。
     */
    void addParameter(String name, String value);

    /**
     * 设置流请求参数集。
     *
     * @param content 流请求参数集。
     */
    void setContent(String content);
}
