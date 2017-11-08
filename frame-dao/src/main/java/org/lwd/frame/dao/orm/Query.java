package org.lwd.frame.dao.orm;

import org.lwd.frame.dao.model.Model;

/**
 * 检索接口。
 *
 * @author lwd
 */
public interface Query {
    /**
     * 获取Model类。
     *
     * @return Model类。
     */
    Class<? extends Model> getModelClass();

    /**
     * 获取数据源。
     *
     * @return 数据源。
     */
    String getDataSource();

    /**
     * 获取SET片段。
     *
     * @return SET片段。
     */
    String getSet();

    /**
     * 获取WHERE片段。
     *
     * @return WHERE片段。
     */
    String getWhere();

    /**
     * 获取GROUP BY片段。
     *
     * @return GROUP BY片段。
     */
    String getGroup();

    /**
     * 获取ORDER BY片段。
     *
     * @return ORDER BY片段。
     */
    String getOrder();

    /**
     * 是否加锁。
     *
     * @return 如果已加锁则返回true；否则返回false（默认）。
     */
    boolean isLocked();

    /**
     * 获取最大返回的记录数。
     *
     * @return 最大返回的记录数。
     */
    int getSize();

    /**
     * 获取当前显示的页码。
     *
     * @return 当前显示的页码。
     */
    int getPage();
}
