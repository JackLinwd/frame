package org.lwd.frame.dao.orm;

import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.model.ModelTable;
import org.lwd.frame.dao.model.ModelTables;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;

import javax.inject.Inject;
import java.util.Collection;

/**
 * ORM支持类。
 *
 * @author lwd
 */
public abstract class OrmSupport<Q extends Query> implements Orm<Q> {
    @Inject
    protected Validator validator;
    @Inject
    protected Logger logger;
    @Inject
    protected ModelTables modelTables;

    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id) {
        return findById(null, modelClass, id, false);
    }

    @Override
    public <T extends Model> T findById(String dataSource, Class<T> modelClass, String id) {
        return findById(dataSource, modelClass, id, false);
    }

    @Override
    public <T extends Model> T findById(Class<T> modelClass, String id, boolean lock) {
        return findById(null, modelClass, id, lock);
    }

    @Override
    public <T extends Model> boolean save(T model) {
        return save(null, model);
    }

    @Override
    public <T extends Model> void save(Collection<T> models) {
        save(null, models);
    }

    @Override
    public <T extends Model> void save(String dataSource, Collection<T> models) {
        if (validator.isEmpty(models))
            return;

        models.forEach(model -> save(dataSource, model));

        if (logger.isDebugEnable())
            logger.debug("批量保存[{}]个Model数据。", models.size());
    }

    @Override
    public <T extends Model> boolean insert(T model) {
        return insert(null, model);
    }

    @Override
    public <T extends Model> boolean delete(T model) {
        return delete(null, model);
    }

    @Override
    public <T extends Model> boolean deleteById(Class<T> modelClass, String id) {
        return deleteById(null, modelClass, id);
    }

    protected <T extends Model, E extends Query> String getDataSource(String dataSource, E query, ModelTable modelTable, Class<T> modelClass) {
        if (dataSource != null)
            return dataSource;

        if (query != null) {
            if (query.getDataSource() != null)
                return query.getDataSource();

            if (modelTable == null)
                modelTable = modelTables.get(query.getModelClass());
        }

        if (modelTable == null && modelClass != null)
            modelTable = modelTables.get(modelClass);
        if (modelTable != null && modelTable.getDataSource() != null)
            return modelTable.getDataSource();

        return "";
    }
}
