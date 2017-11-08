package org.lwd.frame.dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * SQL检索结果二维表。
 *
 * @author lwd
 */
public interface SqlTable {
    /**
     * 获取列名集。
     *
     * @return 列名集。
     */
    String[] getNames();

    /**
     * 获取总行数。
     *
     * @return 总行数。
     */
    int getRowCount();

    /**
     * 获取总列数。
     *
     * @return 总列数。
     */
    int getColumnCount();

    /**
     * 获取数据。
     *
     * @param row    行标，第一行行标为0。
     * @param column 列标，第一列列标为0。
     * @return 数据。
     */
    <T> T get(int row, int column);

    /**
     * 获取数据。
     *
     * @param row        行标，第一行行标为0。
     * @param columnName 列名称。
     * @return 数据。
     */
    <T> T get(int row, String columnName);

    /**
     * 设置数据集。
     *
     * @param rs 数据集。
     * @throws SQLException SQL异常。
     */
    void set(ResultSet rs) throws SQLException;
}
