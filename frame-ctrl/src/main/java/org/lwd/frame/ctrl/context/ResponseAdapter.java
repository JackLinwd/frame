package org.lwd.frame.ctrl.context;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 输出适配器。
 *
 * @author lwd
 */
public interface ResponseAdapter {
    /**
     * 设置类容类型。
     *
     * @param contentType 类容类型。
     */
    void setContentType(String contentType);

    /**
     * 设置头信息。
     *
     * @param name  名称。
     * @param value 值。
     */
    void setHeader(String name, String value);

    /**
     * 获取输出流。
     *
     * @return 输出流。
     */
    OutputStream getOutputStream();

    /**
     * 发送数据。
     *
     * @throws IOException 未处理IO读写异常。
     */
    void send() throws IOException;

    /**
     * 跳转到指定URL地址。
     *
     * @param url 目标URL地址。
     */
    void redirectTo(String url);

    /**
     * 发送错误码。
     *
     * @param code 错误码。
     */
    void sendError(int code);
}
