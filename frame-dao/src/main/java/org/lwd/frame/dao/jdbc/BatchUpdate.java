package org.lwd.frame.dao.jdbc;


/**
 * 搜集更新SQL，并在同一个事务中批量更新。
 *
 * @author lwd
 */
public interface BatchUpdate {
    /**
     * 开始收集。
     */
    void begin();

    /**
     * 设置忽略的SQL片段；如果SQL包含此字符串时将不进行收集。
     *
     * @param sql 忽略的SQL片段。
     */
    void ignore(String sql);

    /**
     * 收集。
     *
     * @param dataSource 数据源。
     * @param sql        SQL。
     * @param args       参数集。
     * @return 如果已收集则返回true；否则返回false。
     */
    boolean collect(String dataSource, String sql, Object[] args);

    /**
     * 提交更新。
     */
    void commit();

    /**
     * 取消。
     */
    void cancel();
}
