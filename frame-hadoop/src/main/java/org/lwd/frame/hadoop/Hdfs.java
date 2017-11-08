package org.lwd.frame.hadoop;

import org.lwd.frame.atomic.Closable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Hadoop文件系统支持。
 *
 * @author lwd
 */
public interface Hdfs extends Closable {
    /**
     * 列出文件。
     *
     * @param path      路径。
     * @param recursive 是否递归子目录。
     * @return 文件集。
     */
    List<String> list(String path, boolean recursive);

    /**
     * 创建路径。
     *
     * @param path 路径。
     */
    void mkdirs(String path);

    /**
     * 获取文件最后修改时间。
     *
     * @param path 路径。
     * @return 最后修改时间。
     */
    long lastModified(String path);

    /**
     * 读取文件。
     *
     * @param path 文件路径。
     * @return 文件内容。
     */
    byte[] read(String path);

    /**
     * 读取文件。
     *
     * @param path         输入文件路径。
     * @param outputStream 输出流。
     */
    void read(String path, OutputStream outputStream);

    /**
     * 写入文件。
     *
     * @param inputStream 输入流。
     * @param path        输出文件路径。
     */
    void write(InputStream inputStream, String path);

    /**
     * 写入文件。
     *
     * @param data 数据。
     * @param path 输出文件路径。
     */
    void write(byte[] data, String path);

    /**
     * 删除文件。
     *
     * @param path 文件路径。
     */
    void delete(String path);

    /**
     * 上传文件。
     *
     * @param file 文件路径。
     * @param path 保存路径。
     */
    void upload(String file, String path);

    /**
     * 下载文件。
     *
     * @param file 文件路径。
     * @param path 保存路径。
     */
    void download(String file, String path);

    /**
     * 获取输入流。
     *
     * @param path 输入文件路径。
     * @return 输入流。
     * @throws IOException 未处理IO异常。
     */
    InputStream getInputStream(String path) throws IOException;

    /**
     * 获取输出流。
     *
     * @param path 输出文件路径。
     * @return 输出流。
     * @throws IOException 未处理IO异常。
     */
    OutputStream getOutputStream(String path) throws IOException;
}
