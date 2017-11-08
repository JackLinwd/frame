package org.lwd.frame.ctrl;

import java.util.Map;

/**
 * @author lwd
 */
public interface Forward {
    /**
     * 跳转并执行指定URI请求。
     *
     * @param uri URI。
     * @return 执行结果。
     */
    Object redirect(String uri);

    /**
     * 跳转并执行指定URI请求。
     *
     * @param uri        URI。
     * @param parameters 参数集。
     * @return 执行结果。
     */
    Object redirect(String uri, Map<String, Object> parameters);

    /**
     * 获取指定参数值。
     *
     * @param name 参数名。
     * @param <T>  参数值类。
     * @return 参数值；如果不存在则返回null。
     */
    <T> T getParameter(String name);

    /**
     * 获取跳转参数集。
     *
     * @return 跳转参数集；如果不存在则返回null。
     */
    Map<String, Object> getParameters();

    /**
     * 跳转到指定URL地址。
     *
     * @param url 目标URL地址。
     */
    void redirectTo(String url);
}
