package org.lwd.frame.dao.orm;

import org.lwd.frame.dao.model.Model;

/**
 * @author lwd
 */
public class QuerySupport implements Query {
    protected Class<? extends Model> modelClass;
    protected String dataSource;
    protected String set;
    protected String where;
    protected String group;
    protected String order;
    protected boolean locked;
    protected int size;
    protected int page;

    @Override
    public Class<? extends Model> getModelClass() {
        return modelClass;
    }

    @Override
    public String getDataSource() {
        return dataSource;
    }

    @Override
    public String getSet() {
        return set;
    }

    @Override
    public String getWhere() {
        return where;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getOrder() {
        return order;
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getPage() {
        return page;
    }
}
