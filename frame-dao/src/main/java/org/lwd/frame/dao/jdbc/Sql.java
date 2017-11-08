package org.lwd.frame.dao.jdbc;

import java.util.List;

/**
 * SQL操作接口。
 *
 * @author lwd
 */
public interface Sql extends Jdbc {
    /**
     * 执行批量更新操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 影响记录数。
     */
    int[] update(String sql, List<Object[]> args);

    /**
     * 执行批量更新操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 影响记录数。
     */
    int[] update(String dataSource, String sql, List<Object[]> args);

    /**
     * 备份数据。
     * 将数据从源表移动到目标表中。
     *
     * @param from  源表名称。
     * @param to    目标表名称。
     * @param where 数据WHERE字句。
     * @param args  参数集。
     * @return 备份记录数。
     */
    int backup(String from, String to, String where, Object[] args);

    /**
     * 备份数据。
     * 将数据从源表移动到目标表中。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param from       源表名称。
     * @param to         目标表名称。
     * @param where      数据WHERE字句。
     * @param args       参数集。
     * @return 备份记录数。
     */
    int backup(String dataSource, String from, String to, String where, Object[] args);
}
