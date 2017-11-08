package org.lwd.frame.dao.orm.hibernate;

import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.orm.Query;
import org.lwd.frame.dao.orm.QuerySupport;

/**
 * Hibernate检索构造器。用于构造HibernateORM检索语句。
 *
 * @author lwd
 */
public class HibernateQuery extends QuerySupport implements Query {
    /**
     * 检索构造器。
     *
     * @param modelClass Model类。
     */
    public <T extends Model> HibernateQuery(Class<T> modelClass) {
        if (modelClass == null)
            throw new NullPointerException("Model类不允许为空！");

        this.modelClass = modelClass;
    }

    /**
     * 设置数据源。
     *
     * @param dataSource 数据源。
     * @return 当前Query实例。
     */
    public HibernateQuery dataSource(String dataSource) {
        this.dataSource = dataSource;

        return this;
    }

    /**
     * 设置SET片段。
     *
     * @param set SET片段。
     * @return 当前Query实例。
     */
    public HibernateQuery set(String set) {
        this.set = set;

        return this;
    }

    /**
     * 设置WHERE片段。
     *
     * @param where WHERE片段。
     * @return 当前Query实例。
     */
    public HibernateQuery where(String where) {
        this.where = where;

        return this;
    }

    /**
     * 设置GROUP BY片段。为空则不分组。
     *
     * @param group GROUP BY片段。
     * @return 当前Query实例。
     */
    public HibernateQuery group(String group) {
        this.group = group;

        return this;
    }

    /**
     * 设置ORDER BY片段。为空则不排序。
     *
     * @param order ORDER BY片段。
     * @return 当前Query实例。
     */
    public HibernateQuery order(String order) {
        this.order = order;

        return this;
    }

    /**
     * 添加悲观锁。
     *
     * @return 当前Query实例。
     */
    public HibernateQuery lock() {
        locked = true;

        return this;
    }

    /**
     * 设置最大返回的记录数。如果小于1则返回全部数据。
     *
     * @param size 最大返回的记录数。
     * @return 当前Query实例。
     */
    public HibernateQuery size(int size) {
        this.size = size;

        return this;
    }

    /**
     * 设置当前显示的页码。只有当size大于0时页码数才有效。如果页码小于1，则默认为1。
     *
     * @param page 当前显示的页码。
     * @return 当前Query实例。
     */
    public HibernateQuery page(int page) {
        this.page = page;

        return this;
    }
}
