package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * PPTx处理器。
 *
 * @author lwd
 */
public interface Pptx {
    /**
     * 输出PPTx。
     *
     * @param object       数据。
     * @param outputStream 输出流。
     */
    void write(JSONObject object, OutputStream outputStream);

    /**
     * 读取并解析PPTx数据。
     *
     * @param inputStream  输入流。
     * @param streamWriter 流数据写入器。
     * @return 数据。
     */
    JSONObject read(InputStream inputStream, StreamWriter streamWriter);
}
