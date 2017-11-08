package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONArray;

import java.io.OutputStream;

/**
 * Excel操作。
 *
 * @author lwd
 */
public interface Excel {
    /**
     * 输出Excel文档。
     *
     * @param titles       标题集，即显示在Excel文档的标题集合。
     * @param names        名称集，即标题对应的JSON数据key值。
     * @param array        数据集。
     * @param outputStream 输出流。
     */
    void write(String[] titles, String[] names, JSONArray array, OutputStream outputStream);
}
