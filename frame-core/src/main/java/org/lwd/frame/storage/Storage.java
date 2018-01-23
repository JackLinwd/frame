package org.lwd.frame.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件存储处理器。
 *
 * @author lwd
 */
public interface Storage {
    /**
     * 获取存储类型。
     *
     * @return 存储类型。
     */
    String getType();

    /**
     * 创建路径。
     *
     * @param path 路径。
     */
    void mkdirs(String path);

    /**
     * 获取绝对路径。
     *
     * @param path 路径。
     * @return 绝对路径。
     */
    String getAbsolutePath(String path);

    /**
     * 验证文件是否存在。
     *
     * @param absolutePath 文件绝对路径。
     * @return 如果存在则返回true；否则返回false。
     */
    boolean exists(String absolutePath);

    /**
     * 获取文件最后修改时间。
     *
     * @param path 文件路径。
     * @return 最后修改时间。
     */
    long lastModified(String path);

    /**
     * 读取文件。
     *
     * @param path         读取文件路径。
     * @param outputStream 输出流。
     * @throws IOException IO异常。
     */
    void read(String path, OutputStream outputStream) throws IOException;

    /**
     * 写入文件。
     *
     * @param path        写入文件路径。
     * @param inputStream 输入流。
     * @throws IOException IO异常。
     */
    void write(String path, InputStream inputStream) throws IOException;

    /**
     * 写入文金。
     *
     * @param path  写入文件路径。
     * @param bytes 数据。
     * @throws IOException IO异常。
     */
    void write(String path, byte[] bytes) throws IOException;

    /**
     * 获取输入流。
     *
     * @param path 输入文件路径。
     * @return 输入流。
     * @throws IOException IO异常。
     */
    InputStream getInputStream(String path) throws IOException;

    /**
     * 获取输出流。
     *
     * @param path 输出文件路径。
     * @return 输出流。
     * @throws IOException IO异常。
     */
    OutputStream getOutputStream(String path) throws IOException;

    /**
     * 删除文件。
     *
     * @param path 文件路径。
     */
    void delete(String path);
}
