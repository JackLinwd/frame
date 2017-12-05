package org.lwd.frame.test;

import java.util.List;
import java.util.Map;

/**
 * HTTP切片。主要对HTTP请求的控制。
 *
 * @author lwd
 */
public interface HttpAspect {
    /**
     * 重置环境。
     */
    void reset();

    /**
     * 替换GET请求。
     *
     * @param urls       请求URL地址集，用于保存GET请求的URL地址。
     * @param headers    请求头信息集，用于保存GET请求的头信息。
     * @param parameters 请求参数集，用于保存GET请求的参数信息。
     * @param contents   请求结果集，用于返回GET请求的结果。
     */
    void get(List<String> urls, List<Map<String, String>> headers, List<Object> parameters, List<String> contents);

    /**
     * 替换POST请求。
     *
     * @param urls       请求URL地址集，用于保存POST请求的URL地址。
     * @param headers    请求头信息集，用于保存POST请求的头信息。
     * @param parameters 请求参数集，用于保存POST请求的参数信息。
     * @param contents   请求结果集，用于返回POST请求的结果。
     */
    void post(List<String> urls, List<Map<String, String>> headers, List<Object> parameters, List<String> contents);
}
