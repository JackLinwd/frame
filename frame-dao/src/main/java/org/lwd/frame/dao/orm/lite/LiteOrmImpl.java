package org.lwd.frame.dao.orm.lite;

import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.dao.jdbc.Connection;
import org.lwd.frame.dao.jdbc.DataSource;
import org.lwd.frame.dao.jdbc.Sql;
import org.lwd.frame.dao.jdbc.SqlTable;
import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.model.ModelHelper;
import org.lwd.frame.dao.model.ModelTable;
import org.lwd.frame.dao.orm.OrmSupport;
import org.lwd.frame.dao.orm.PageList;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Numeric;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwd
 */
@Repository("frame.dao.orm.lite")
public class LiteOrmImpl extends OrmSupport<LiteQuery> implements LiteOrm {
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Generator generator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private DataSource dataSource;
    @Inject
    private Connection connection;
    @Inject
    private Sql sql;

    @Override
    public <T extends Model> T findById(String dataSource, Class<T> modelClass, String id, boolean lock) {
        if (validator.isEmpty(id))
            return null;

        LiteQuery query = new LiteQuery(modelClass).dataSource(dataSource).where(modelTables.get(modelClass).getIdColumnName() + "=?");
        if (lock)
            query.lock();

        return queryOne(query, new Object[]{id});
    }

    @Override
    public <T extends Model> T findOne(LiteQuery query, Object[] args) {
        return queryOne(query.page(1).size(1), args);
    }

    @SuppressWarnings("unchecked")
    private <T extends Model> T queryOne(LiteQuery query, Object[] args) {
        List<T> list = (List<T>) query(query.countable(false), args).getList();

        return validator.isEmpty(list) ? null : list.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Model> PageList<T> query(LiteQuery query, Object[] args) {
        PageList<T> models = BeanFactory.getBean(PageList.class);
        if (query.getSize() > 0)
            models.setPage(query.isCountable() ? count(query, args) : query.getSize() * query.getPage(), query.getSize(), query.getPage());
        models.setList(new ArrayList<>());

        ModelTable modelTable = modelTables.get(query.getModelClass());
        String querySql = getQuerySql(query, modelTable, models.getSize(), models.getNumber());
        SqlTable sqlTable = sql.query(getDataSource(null, query, modelTable, null), querySql, args);
        if (sqlTable.getRowCount() == 0)
            return models;

        String[] columnNames = sqlTable.getNames();
        for (int i = 0; i < sqlTable.getRowCount(); i++) {
            T model = BeanFactory.getBean((Class<T>) query.getModelClass());
            for (int j = 0; j < columnNames.length; j++) {
                if (columnNames[j].equals(modelTable.getIdColumnName()))
                    model.setId(sqlTable.get(i, j));
                else
                    modelTable.set(model, columnNames[j], sqlTable.get(i, j));
            }
            models.getList().add(model);
        }

        return models;
    }

    private String getQuerySql(LiteQuery query, ModelTable modelTable, int size, int page) {
        StringBuilder querySql = new StringBuilder().append("SELECT ").append(validator.isEmpty(query.getSelect()) ? "*" : query.getSelect());
        append(query, querySql, modelTable);
        if (!validator.isEmpty(query.getOrder()))
            querySql.append(" ORDER BY ").append(query.getOrder());
        if (query.isLocked()) {
            connection.beginTransaction();
            querySql.append(" FOR UPDATE");
        } else if (size > 0)
            dataSource.getDialects().get(getDataSource(null, query, modelTable, null))
                    .appendPagination(querySql, size, page < 1 ? 1 : page);

        return querySql.toString();
    }

    @Override
    public int count(LiteQuery query, Object[] args) {
        StringBuilder querySql = new StringBuilder().append("SELECT COUNT(*)");
        ModelTable modelTable = modelTables.get(query.getModelClass());
        append(query, querySql, modelTable);

        SqlTable sqlTable = sql.query(getDataSource(null, query, modelTable, null), querySql.toString(), args);
        if (sqlTable.getRowCount() == 0)
            return 0;

        return numeric.toInt(sqlTable.get(0, 0));
    }

    private void append(LiteQuery query, StringBuilder sql, ModelTable modelTable) {
        sql.append(" FROM ").append(getFrom(query, modelTable));
        if (query.getIndex() != null)
            sql.append(query.getIndex().get());
        if (!validator.isEmpty(query.getWhere()))
            sql.append(" WHERE ").append(query.getWhere());
        if (!validator.isEmpty(query.getGroup()))
            sql.append(" GROUP BY ").append(query.getGroup());
    }

    private String getFrom(LiteQuery query, ModelTable modelTable) {
        if (!validator.isEmpty(query.getFrom()))
            return query.getFrom();

        if (!validator.isEmpty(modelTable.getMemoryName()))
            return modelTable.getMemoryName();

        return modelTable.getTableName();
    }

    @Override
    public <T extends Model> boolean save(String dataSource, T model) {
        if (model == null) {
            logger.warn(null, "要保存的Model为null！");

            return false;
        }

        if (validator.isEmpty(model.getId()))
            model.setId(null);

        ModelTable modelTable = modelTables.get(model.getClass());
        if (model.getId() == null)
            return insert(dataSource, model, modelTable) == 1;

        return update(dataSource, model, modelTable) == 1;
    }

    @Override
    public <T extends Model> boolean insert(String dataSource, T model) {
        if (model == null) {
            logger.warn(null, "要保存的Model为null！");

            return false;
        }

        return insert(dataSource, model, modelTables.get(model.getClass())) > 0;
    }

    /**
     * 新增。
     *
     * @param model      Model实例。
     * @param modelTable Model-Table映射关系表。
     * @return 影响记录数。
     */
    private <T extends Model> int insert(String dataSource, T model, ModelTable modelTable) {
        StringBuilder insertSql = new StringBuilder().append("INSERT INTO ").append(modelTable.getTableName()).append('(');
        List<Object> args = new ArrayList<>();
        if (model.getId() == null && modelTable.isUuid())
            model.setId(generator.uuid());
        int columnCount = 0;
        if (model.getId() != null) {
            insertSql.append(modelTable.getIdColumnName());
            args.add(model.getId());
            columnCount++;
        }
        for (String columnName : modelTable.getColumnNames()) {
            if (modelTable.isNative(columnName))
                continue;

            if (columnCount > 0)
                insertSql.append(',');
            insertSql.append(columnName);
            args.add(modelTable.get(model, columnName));
            columnCount++;
        }
        insertSql.append(") VALUES(");
        for (int i = 0; i < columnCount; i++) {
            if (i > 0)
                insertSql.append(',');
            insertSql.append('?');
        }
        insertSql.append(")");

        return update(dataSource, modelTable, insertSql, args.toArray());
    }

    /**
     * 更新。
     *
     * @param model      Model实例。
     * @param modelTable Model-Table映射关系表。
     * @return 影响记录数。
     */
    private <T extends Model> int update(String dataSource, T model, ModelTable modelTable) {
        StringBuilder updateSql = new StringBuilder().append("UPDATE ").append(modelTable.getTableName()).append(" SET ");
        List<Object> args = new ArrayList<>();
        for (String columnName : modelTable.getColumnNames()) {
            if (modelTable.isNative(columnName))
                continue;

            if (!args.isEmpty())
                updateSql.append(',');
            updateSql.append(columnName).append("=?");
            args.add(modelTable.get(model, columnName));
        }
        updateSql.append(" WHERE ").append(modelTable.getIdColumnName()).append("=?");
        args.add(model.getId());

        return update(dataSource, modelTable, updateSql, args.toArray());
    }

    @Override
    public boolean update(LiteQuery query, Object[] args) {
        ModelTable modelTable = modelTables.get(query.getModelClass());
        StringBuilder updateSql = new StringBuilder().append("UPDATE ").append(modelTable.getTableName()).append(" SET ").append(query.getSet());
        if (!validator.isEmpty(query.getWhere()))
            updateSql.append(" WHERE ").append(query.getWhere());

        return update(query.getDataSource(), modelTable, updateSql, args) > 0;
    }

    private int update(String dataSource, ModelTable modelTable, StringBuilder updateSql, Object[] args) {
        String sql = updateSql.toString();
        int n = this.sql.update(dataSource = getDataSource(dataSource, null, modelTable, null), sql, args);
        if (!validator.isEmpty(modelTable.getMemoryName()))
            this.sql.update(dataSource, sql.replaceFirst(modelTable.getTableName(), modelTable.getMemoryName()), args);

        return n;
    }

    @Override
    public <T extends Model> boolean delete(String dataSource, T model) {
        return model != null
                && !validator.isEmpty(model.getId())
                && delete(new LiteQuery(model.getClass()).dataSource(dataSource)
                .where(modelTables.get(model.getClass()).getIdColumnName() + "=?"), new Object[]{model.getId()});
    }

    @Override
    public boolean delete(LiteQuery query, Object[] args) {
        ModelTable modelTable = modelTables.get(query.getModelClass());
        StringBuilder deleteSql = new StringBuilder().append("DELETE FROM ").append(modelTable.getTableName());
        if (!validator.isEmpty(query.getWhere()))
            deleteSql.append(" WHERE ").append(query.getWhere());

        return update(query.getDataSource(), modelTable, deleteSql, args) > 0;
    }

    @Override
    public <T extends Model> boolean deleteById(String dataSource, Class<T> modelClass, String id) {
        return delete(new LiteQuery(modelClass).dataSource(dataSource).where(modelTables.get(modelClass).getIdColumnName() + "=?"),
                new Object[]{id});
    }

    @Override
    public void resetMemory(Class<? extends Model> modelClass) {
        resetMemory(null, modelClass);
    }

    @Override
    public void resetMemory(String dataSource, Class<? extends Model> modelClass) {
        ModelTable modelTable = modelTables.get(modelClass);
        if (modelClass == null) {
            logger.warn(null, "ModelClass不存在！");

            return;
        }

        if (validator.isEmpty(modelTable.getMemoryName())) {
            logger.warn(null, "Model[{}]未定义内存映射表！", modelClass);

            return;
        }

        sql.update(dataSource = getDataSource(dataSource, null, modelTable, null), "DELETE FROM "
                + modelTable.getMemoryName(), new Object[0]);
        sql.update(dataSource, "INSERT INTO " + modelTable.getMemoryName() + " SELECT * FROM " + modelTable.getTableName(), new Object[0]);

        if (logger.isDebugEnable())
            logger.debug("内存表[{}]重置完成。", modelClass);
    }

    @Override
    public void fail(Throwable throwable) {
        sql.fail(throwable);
    }

    @Override
    public void close() {
        sql.close();
    }
}
