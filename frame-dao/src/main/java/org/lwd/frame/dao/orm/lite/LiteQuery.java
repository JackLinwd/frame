package org.lwd.frame.dao.orm.lite;

import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.orm.Query;
import org.lwd.frame.dao.orm.QuerySupport;

/**
 * Lite检索构造器。用于构造非级联ORM检索语句。
 *
 * @author lwd
 */
public class LiteQuery extends QuerySupport implements Query {
    private String select;
    private String from;

    /**
     * 检索构造器。
     *
     * @param modelClass Model类。
     */
    public <T extends Model> LiteQuery(Class<T> modelClass) {
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
    public LiteQuery dataSource(String dataSource) {
        this.dataSource = dataSource;

        return this;
    }

    /**
     * 设置SELECT字段集，如果为空则为所有字段。
     *
     * @param select SELECT字段集。
     * @return 当前Query实例。
     */
    public LiteQuery select(String select) {
        this.select = select;

        return this;
    }

    /**
     * 设置FROM表名称集，至少必须包含一个表名称。如果为空则使用Model类对应的表名称。
     *
     * @param from FROM表名称集。
     * @return 当前Query实例。
     */
    public LiteQuery from(String from) {
        this.from = from;

        return this;
    }

    /**
     * 设置SET片段。
     *
     * @param set SET片段。
     * @return 当前Query实例。
     */
    public LiteQuery set(String set) {
        this.set = set;

        return this;
    }

    /**
     * 设置WHERE片段。
     *
     * @param where WHERE片段。
     * @return 当前Query实例。
     */
    public LiteQuery where(String where) {
        this.where = where;

        return this;
    }

    /**
     * 设置ORDER BY片段。为空则不排序。
     *
     * @param order ORDER BY片段。
     * @return 当前Query实例。
     */
    public LiteQuery order(String order) {
        this.order = order;

        return this;
    }

    /**
     * 设置GROUP BY片段。为空则不分组。
     *
     * @param group GROUP BY片段。
     * @return 当前Query实例。
     */
    public LiteQuery group(String group) {
        this.group = group;

        return this;
    }

    /**
     * 添加悲观锁。
     *
     * @return 当前Query实例。
     */
    public LiteQuery lock() {
        locked = true;

        return this;
    }

    /**
     * 设置最大返回的记录数。如果小于1则返回全部数据。
     *
     * @param size 最大返回的记录数。
     * @return 当前Query实例。
     */
    public LiteQuery size(int size) {
        this.size = size;

        return this;
    }

    /**
     * 设置当前显示的页码。只有当size大于0时页码数才有效。如果页码小于1，则默认为1。
     *
     * @param page 当前显示的页码。
     * @return 当前Query实例。
     */
    public LiteQuery page(int page) {
        this.page = page;

        return this;
    }

    /**
     * 获取SELECT字段集。
     *
     * @return SELECT字段集。
     */
    public String getSelect() {
        return select;
    }

    /**
     * 获取FROM表名称集。
     *
     * @return FROM表名称集。
     */
    public String getFrom() {
        return from;
    }
}
