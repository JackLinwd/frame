package org.lwd.frame.ctrl.template.excel;

import java.io.OutputStream;
import java.util.Collection;

/**
 * Excel构建器。
 *
 * @author lwd
 */
public interface ExcelBuilder {
    /**
     * 构建Excel。
     *
     * @param titles     标题集，即显示在Excel文档的标题集合。
     * @param names      名称集，即标题对应的JSON数据key值。
     * @param collection 数据集。
     * @return 当前实例。
     */
    ExcelBuilder build(String[] titles, String[] names, Collection<?> collection);

    /**
     * 构建下载。
     *
     * @param name 下载文件名。
     * @return 当前实例。
     */
    ExcelBuilder download(String name);

    /**
     * 输出。
     *
     * @param outputStream 输出流。
     */
    void write(OutputStream outputStream);
}
