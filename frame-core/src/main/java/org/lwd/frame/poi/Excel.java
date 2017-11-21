package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONArray;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

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

    /** 读取xls
     * 判断文件的sheet是否存在.
     *
     * @param fileDir   文件路径
     * @param sheetName 表格索引名
     * @return
     */
    boolean sheetExist(String fileDir, String sheetName);

    /** 读取xls
     * 判断文件的sheet是否存在.
     *
     * @param fileDir   文件路径
     * @param sheetName 表格索引名
     * @return
     */
    boolean sheetXlsExist(String fileDir, String sheetName);

    /** 读取xlsx
     * 判断文件的sheet是否存在.
     *
     * @param fileDir   文件路径
     * @param sheetName 表格索引名
     * @return
     */
    boolean sheetXlsxExist(String fileDir, String sheetName);

    /**
     * 创建新excel.
     *
     * @param fileDir   excel的路径
     * @param sheetName 要创建的表格索引
     * @param titleRow  excel的第一行即表格头
     */
    void createExcel(String fileDir, String sheetName, String titleRow[]);

    /**
     * 往excel中写入(已存在的数据无法写入).
     *
     * @param fileDir   文件路径
     * @param sheetName 表格索引
     * @param object
     */
    void writeToExcel(String fileDir, String sheetName, Object object);

    /**
     * 读取excel表中的数据.
     *
     * @param fileDir   文件路径
     * @param sheetName 表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1)
     * @param object    object
     */
    List readFromXlsExcel(String fileDir, String sheetName, Object object) throws IOException;

    /**
     * 读取excel表中的数据.
     *
     * @param fileDir   文件路径
     * @param sheetName 表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1)
     * @param object    object
     */
    List readFromXlsxExcel(String fileDir, String sheetName, Object object) throws IOException;

    /**
     * 读取excel表中的数据.
     *
     * @param fileDir   文件路径
     * @param sheetName 表格索引(EXCEL 是多表文档,所以需要输入表索引号，如sheet1)
     * @param object    object
     */
    List readFromExcel(String fileDir, String sheetName, Object object);
}
