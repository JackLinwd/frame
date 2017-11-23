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
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String get(String url, Map<String, String> headers, Map<String, String> parameters);

    /**
     * 通过GET方式获取远程页面数据。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param charset    编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String get(String url, Map<String, String> headers, Map<String, String> parameters, String charset);

    /**
     * 通过GET方式获取远程页面数据。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String get(String url, Map<String, String> headers, String parameters);

    /**
     * 通过GET方式获取远程页面数据。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param charset    编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String get(String url, Map<String, String> headers, String parameters, String charset);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> headers, Map<String, String> parameters);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param charset    编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> headers, Map<String, String> parameters, String charset);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url     目标URL地址。
     * @param headers HTTP头信息集。
     * @param content 参数内容。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> headers, String content);

    /**
     * 通过POST方式获取远程页面数据。
     *
     * @param url     目标URL地址。
     * @param headers HTTP头信息集。
     * @param content 参数内容。
     * @param charset 编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String post(String url, Map<String, String> headers, String content, String charset);

    /**
     * 上传文件。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param files      文件集。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String upload(String url, Map<String, String> headers, Map<String, String> parameters, Map<String, File> files);

    /**
     * 上传文件。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param files      文件集。
     * @param charset    编码，为null则使用默认配置。
     * @return 如果成功则返回页面数据；否则返回null。
     */
    String upload(String url, Map<String, String> headers, Map<String, String> parameters, Map<String, File> files, String charset);

    /**
     * 下载文件。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param dest       下载文件保存路径。
     * @return HTTP请求返回头部信息集。
     */
    Map<String, String> download(String url, Map<String, String> headers, Map<String, String> parameters, String dest);

    /**
     * 下载文件。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param charset    编码，为null则使用默认配置。
     * @param dest       下载文件保存路径。
     * @return HTTP请求返回头部信息集。
     */
    Map<String, String> download(String url, Map<String, String> headers, Map<String, String> parameters, String charset, String dest);

    /**
     * 下载文件。
     *
     * @param url        目标URL地址。
     * @param headers    HTTP头信息集。
     * @param parameters 参数集。
     * @param dest       下载文件保存路径。
     * @return HTTP请求返回头部信息集。
     */
    Map<String, String> download(String url, Map<String, String> headers, String parameters, String dest);

    /**
     * 下载文件。
     *
     * @param url          目标URL地址。
     * @param headers      HTTP头信息集。
     * @param parameters   参数集。
     * @param outputStream 下载文件输出流。
     * @return HTTP请求返回头部信息集。
     */
    Map<String, String> download(String url, Map<String, String> headers, String parameters, OutputStream outputStream);

    /**
     * 获取最近一次请求返回状态码。
     *
     * @return 最近一次请求返回状态码；如果获取失败则返回0。
     */
    int getStatusCode();
}
