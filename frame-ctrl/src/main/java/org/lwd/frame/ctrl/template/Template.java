package org.lwd.frame.ctrl.template;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 输出模板。
 *
 * @author lwd
 */
public interface Template {
    /**
     * 获取输出模板类型。
     *
     * @return 输出模板类型。
     */
    String getType();

    /**
     * 获取当前请求输出内容类型。
     *
     * @return 当前请求输出内容类型。
     */
    String getContentType();

    /**
     * 封装验证失败结果信息。
     *
     * @param code      验证失败编码。
     * @param message   验证失败错误信息。
     * @param parameter 验证参数名。
     * @param value     验证参数值。
     * @return 失败结果信息。
     */
    Object failure(int code, String message, String parameter, String value);

    /**
     * 封装执行成功返回结果说明。
     *
     * @param data 数据。
     * @param key  资源key。
     * @param args 参数值集。
     * @return 执行成功返回结果说明。
     */
    Object success(Object data, String key, Object... args);

    /**
     * 处理模版输出。
     *
     * @param name         模版文件名。
     * @param data         数据。
     * @param outputStream 输出流。
     * @throws IOException 未处理IOException异常。
     */
    void process(String name, Object data, OutputStream outputStream) throws IOException;
}
