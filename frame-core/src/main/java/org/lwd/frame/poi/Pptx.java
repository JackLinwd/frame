package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONObject;

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
}
