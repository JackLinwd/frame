package org.lwd.frame.dao.jdbc;

/**
 * 存储过程操作接口。
 *
 * @author lwd
 */
public interface Procedure extends Jdbc {
    /**
     * 执行检索操作。
     *
     * @param sql  SQL。
     * @param args 参数集。
     * @return 数据集。
     */
    <T> T queryObject(String sql, Object[] args);

    /**
     * 执行检索操作。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 数据集。
     */
    <T> T queryObject(String dataSource, String sql, Object[] args);
}
