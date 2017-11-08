package org.lwd.frame.util;

import java.io.OutputStream;

/**
 * 压缩与解压缩。
 *
 * @author lwd
 */
public interface Compresser {
    /**
     * 使用ZIP压缩。
     *
     * @param bytes 待压缩的数据。
     * @return 压缩后的数据。
     */
    byte[] zip(byte[] bytes);

    /**
     * 使用ZIP压缩。
     *
     * @param bytes        待压缩的数据。
     * @param outputStream 压缩后的输出流。
     */
    void zip(byte[] bytes, OutputStream outputStream);

    /**
     * 解压缩ZIP数据。
     *
     * @param bytes 待解压缩的数据。
     * @return 解压缩后的数据。
     */
    byte[] unzip(byte[] bytes);

    /**
     * 解压缩ZIP数据。
     *
     * @param bytes        待解压缩的数据。
     * @param outputStream 解压缩后的输出流。
     */
    void unzip(byte[] bytes, OutputStream outputStream);
}
