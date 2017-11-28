package org.lwd.frame.util;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

/**
 * HTTP连接器。用于提交HTTP访问。
 *
 * @author lwd
 */
public interface Http {
    /**
     * 通过GET方式获取远程页面数据。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String get(String url, Map<String, String> requestHeaders, Map<String, String> parameters);

    /**
     * 通过GET方式获取远程页面数据。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @param charset        编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String get(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset);

    /**
     * 通过GET方式获取远程页面数据。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String get(String url, Map<String, String> requestHeaders, String parameters);

    /**
     * 通过GET方式获取远程页面数据。
     *
     * @param url             目标URL地址。
     * @param requestHeaders  HTTP请求头信息集。
     * @param parameters      参数集。
     * @param responseHeaders HTTP返回头信息集，用于接收返回的头信息。
     * @param outputStream    HTTP返回数据输出流。
     */
    void get(String url, Map<String, String> requestHeaders, String parameters,
             Map<String, String> responseHeaders, OutputStream outputStream);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> requestHeaders, Map<String, String> parameters);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @param charset        编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url             目标URL地址。
     * @param requestHeaders  HTTP请求头信息集。
     * @param parameters      参数集。
     * @param charset         编码，为null则使用默认配置。
     * @param responseHeaders HTTP返回头信息集，用于接收返回的头信息。
     * @param outputStream    HTTP返回数据输出流。
     */
    void post(String url, Map<String, String> requestHeaders, Map<String, String> parameters, String charset,
              Map<String, String> responseHeaders, OutputStream outputStream);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param content        参数内容。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> requestHeaders, String content);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param content        参数内容。
     * @param charset        编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> requestHeaders, String content, String charset);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url             目标URL地址。
     * @param requestHeaders  HTTP请求头信息集。
     * @param content         参数内容。
     * @param charset         编码，为null则使用默认配置。
     * @param responseHeaders HTTP返回头信息集，用于接收返回的头信息。
     * @param outputStream    HTTP返回数据输出流。
     */
    void post(String url, Map<String, String> requestHeaders, String content, String charset,
              Map<String, String> responseHeaders, OutputStream outputStream);

    /**
     * 上传文件。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @param files          文件集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String upload(String url, Map<String, String> requestHeaders, Map<String, String> parameters, Map<String, File> files);

    /**
     * 上传文件。
     *
     * @param url            目标URL地址。
     * @param requestHeaders HTTP头信息集。
     * @param parameters     参数集。
     * @param files          文件集。
     * @param charset        编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String upload(String url, Map<String, String> requestHeaders, Map<String, String> parameters, Map<String, File> files, String charset);

    /**
     * 获取最近一次请求返回状态码。
     *
     * @return 最近一次请求返回状态码；如果获取失败则返回0。
     */
    int getStatusCode();
}
