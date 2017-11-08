package org.lwd.frame.dao.orm;

import org.lwd.frame.atomic.Atomicable;
import org.lwd.frame.dao.model.Model;

import java.util.Collection;

/**
 * ORM接口。
 *
 * @author lwd
 */
public interface Orm<Q extends Query> extends Atomicable {
    /**
     * 根据ID值获取Model实例。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(Class<T> modelClass, String id);

    /**
     * 根据ID值获取Model实例。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param modelClass Model类。
     * @param id         ID值。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(String dataSource, Class<T> modelClass, String id);

    /**
     * 根据ID值获取Model实例。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @param lock       是否加悲观锁。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(Class<T> modelClass, String id, boolean lock);

    /**
     * 根据ID值获取Model实例。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param modelClass Model类。
     * @param id         ID值。
     * @param lock       是否加悲观锁。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findById(String dataSource, Class<T> modelClass, String id, boolean lock);

    /**
     * 检索一条满足条件的数据。如果存在多条满足条件的数据则只返回第一条数据。
     *
     * @param query 检索条件。
     * @param args  参数集。
     * @return Model实例，如果不存在则返回null。
     */
    <T extends Model> T findOne(Q query, Object[] args);

    /**
     * 检索满足条件的数据。
     *
     * @param query 检索条件。
     * @param args  参数集。
     * @return Model实例集。
     */
    <T extends Model> PageList<T> query(Q query, Object[] args);

    /**
     * 计算满足条件的数据数。
     *
     * @param query 检索条件。
     * @param args  参数集。
     * @return 数据数。
     */
    int count(Q query, Object[] args);

    /**
     * 保存Model。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param model 要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean save(T model);

    /**
     * 保存Model。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param model      要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean save(String dataSource, T model);

    /**
     * 保存Model集。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param models 要保存的Model集。
     * @param <T>    Model类。
     */
    <T extends Model> void save(Collection<T> models);

    /**
     * 保存Model集。
     * 如果要保存Model实例的ID为null则执行新增操作，否则执行更新操作。新增时将自动创建一个随机ID。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param models     要保存的Model集。
     * @param <T>        Model类。
     */
    <T extends Model> void save(String dataSource, Collection<T> models);

    /**
     * 新增Model。ID由业务系统或数据库控制。
     *
     * @param model 要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean insert(T model);

    /**
     * 新增Model。ID由业务系统或数据库控制。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param model      要保存的Model。
     * @return 如果保存成功则返回true；否则返回false。
     */
    <T extends Model> boolean insert(String dataSource, T model);

    /**
     * 批量更新数据。
     *
     * @param query 更新条件。
     * @param args  参数集。
     * @return 如果删除成功则返回true；否则返回false。
     */
    boolean update(Q query, Object[] args);

    /**
     * 删除Model。
     *
     * @param model 要删除的Model。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean delete(T model);

    /**
     * 删除Model。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param model      要删除的Model。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean delete(String dataSource, T model);

    /**
     * 批量删除数据。
     *
     * @param query 删除条件。
     * @param args  参数集。
     * @return 如果删除成功则返回true；否则返回false。
     */
    boolean delete(Q query, Object[] args);

    /**
     * 删除指定ID值的Model实例。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean deleteById(Class<T> modelClass, String id);

    /**
     * 删除指定ID值的Model实例。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param modelClass Model类。
     * @param id         ID值。
     * @return 如果删除成功则返回true；否则返回false。
     */
    <T extends Model> boolean deleteById(String dataSource, Class<T> modelClass, String id);
}
