package org.lwd.frame.freemarker;

import java.io.OutputStream;

/**
 * 模板文件解析器。
 *
 * @author lwd
 */
public interface Freemarker {
    /**
     * 是否存在字符串模板。
     *
     * @param name 模板名称。
     * @return 如果存在则返回true；否则返回false。
     */
    boolean containsStringTemplate(String name);

    /**
     * 设置字符串模板。
     *
     * @param name     模板名称。
     * @param template 模板内容。
     */
    void putStringTemplate(String name, String template);

    /**
     * 移除字符串模板。
     *
     * @param name 模板名称。
     */
    void removeStringTemplate(String name);

    /**
     * 解析模板并输出为字符串。
     *
     * @param name 模板文件名称。
     * @param data 数据。
     * @return 解析后的字符串；如果解析失败则返回null。
     */
    String process(String name, Object data);

    /**
     * 解析模板并输出到输出流中。
     *
     * @param name   模板文件名称。
     * @param data   数据。
     * @param output 输出流。
     */
    void process(String name, Object data, OutputStream output);
}
