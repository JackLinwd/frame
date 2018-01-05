package org.lwd.frame.poi;

import java.io.IOException;
import java.io.InputStream;

/**
 * 流数据写入器。
 * 用于保存文件中的流数据。
 *
 * @author lwd
 */
public interface StreamWriter {
    /**
     * 写入。
     *
     * @param contentType 数据格式。
     * @param filename    文件名。
     * @param inputStream 数据流。
     * @return 文件保存位置。
     * @throws IOException 未处理IO异常。
     */
    String write(String contentType, String filename, InputStream inputStream) throws IOException;
}