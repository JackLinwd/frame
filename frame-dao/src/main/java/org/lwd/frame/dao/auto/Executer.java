package org.lwd.frame.dao.auto;

/**
 * @author lwd
 */
interface Executer {
    /**
     * 保存SQL。
     *
     * @param dataSource 数据源。
     * @param sql        执行的SQL。
     * @param state0     是否检查状态0，如果是则仅当已存在数据状态均不为0时执行。
     * @return 影响数据行数。
     */
    int execute(String dataSource, String sql, boolean state0);
}
