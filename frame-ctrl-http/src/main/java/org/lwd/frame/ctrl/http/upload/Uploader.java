package org.lwd.frame.ctrl.http.upload;

import org.lwd.frame.ctrl.upload.UploadReader;

import java.io.IOException;
import java.util.List;

/**
 * 上传处理器。
 *
 * @author lwd
 */
public interface Uploader {
    /**
     * 获取处理器名称。
     *
     * @return 处理器名称。
     */
    String getName();

    /**
     * 处理上传并返回输出数据。
     *
     * @param readers 上传读取器集。
     * @return 输出数据。
     * @throws IOException IO异常。
     */
    byte[] upload(List<UploadReader> readers) throws IOException;
}