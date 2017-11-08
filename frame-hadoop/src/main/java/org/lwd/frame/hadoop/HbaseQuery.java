package org.lwd.frame.hadoop;

import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.lwd.frame.dao.model.Model;

/**
 * @author lwd
 */
public class HbaseQuery {
    enum Where {
        /**
         * 等于。
         */
        Equals,
        /**
         * 不等于。
         */
        NotEquals,
        /**
         * 小于。
         */
        Less,
        /**
         * 小于等于。
         */
        LessEquals,
        /**
         * 大于。
         */
        Greater,
        /**
         * 大于等于。
         */
        GreaterEquals
    }

    protected Class<? extends Model> modelClass;
    protected String tableName;
    protected FilterList filters;
    protected long pageSize;

    /**
     * 构造HbaseQuery。
     *
     * @param modelClass Model类。
     */
    public HbaseQuery(Class<? extends Model> modelClass) {
        this.modelClass = modelClass;
    }

    /**
     * 构造HbaseQuery。
     *
     * @param tableName 表名称。
     */
    public HbaseQuery(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 增加WHERE条件。
     *
     * @param column 列名。
     * @param where  条件。
     * @param value  值。
     * @return 当前HbaseQuery实例。
     */
    public HbaseQuery addWhere(String column, Where where, String value) {
        addFilter(new SingleColumnValueFilter(Bytes.toBytes(column), null, getCompare(where), Bytes.toBytes(value)));

        return this;
    }

    /**
     * 设置最大显示数量。
     *
     * @param pageSize 最大显示数量。
     * @return 当前HbaseQuery实例。
     */
    public HbaseQuery size(long pageSize) {
        this.pageSize = pageSize;

        return this;
    }

    protected synchronized void addFilter(Filter filter) {
        if (filters == null)
            filters = new FilterList();
        filters.addFilter(filter);
    }

    protected CompareFilter.CompareOp getCompare(Where where) {
        if (where == Where.Equals)
            return CompareFilter.CompareOp.EQUAL;

        if (where == Where.NotEquals)
            return CompareFilter.CompareOp.NOT_EQUAL;

        if (where == Where.Less)
            return CompareFilter.CompareOp.LESS;

        if (where == Where.LessEquals)
            return CompareFilter.CompareOp.LESS_OR_EQUAL;

        if (where == Where.Greater)
            return CompareFilter.CompareOp.GREATER;

        if (where == Where.GreaterEquals)
            return CompareFilter.CompareOp.GREATER_OR_EQUAL;

        return CompareFilter.CompareOp.EQUAL;
    }

    /**
     * 获取Model类。
     *
     * @return Model类。
     */
    public Class<? extends Model> getModelClass() {
        return modelClass;
    }

    /**
     * 获取表名称。
     *
     * @return 表名称。
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 获取过滤器。
     *
     * @return 过滤器。
     */
    public synchronized Filter getFilter() {
        if (pageSize > 0) {
            addFilter(new PageFilter(pageSize));
            pageSize = 0L;
        }

        return filters;
    }
}
