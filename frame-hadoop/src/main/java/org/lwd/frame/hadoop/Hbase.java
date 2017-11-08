package org.lwd.frame.hadoop;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.dao.model.Model;

import java.util.List;
import java.util.Map;

/**
 * HBase数据库支持。
 *
 * @author lwd
 */
public interface Hbase {
    /**
     * 检索Model数据。
     *
     * @param modelClass Model类。
     * @param id         ID值。
     * @param <T>        Model类型。
     * @return Model实例，如果未找到则返回null。
     */
    <T extends Model> T findById(Class<T> modelClass, String id);

    /**
     * 检索数据。
     *
     * @param tableName 表名称。
     * @param id        ID值。
     * @return 数据，如果未找到则返回null。
     */
    JSONObject findById(String tableName, String id);

    /**
     * 检索一个满足条件的Model数据。
     *
     * @param query 检索条件。
     * @param <T>   Model类。
     * @return Model实例，如果未找到则返回null。
     */
    <T extends Model> T findOne(HbaseQuery query);

    /**
     * 检索一个满足条件的数据。
     *
     * @param query 检索条件。
     * @return 数据，如果未找到则返回null。
     */
    JSONObject findOneAsJson(HbaseQuery query);

    /**
     * 检索Model数据集。
     *
     * @param query 检索条件。
     * @param <T>   Model类型。
     * @return Model数据集，如果未找到则返回空集。
     */
    <T extends Model> List<T> query(HbaseQuery query);

    /**
     * 检索JSON格式数据集。
     *
     * @param query 检索条件。
     * @return JSON数据集，如果未找到则返回空集。
     */
    JSONArray queryAsJson(HbaseQuery query);

    /**
     * 保存数据。
     *
     * @param model Model对象。
     * @param <T>   Model类型。
     */
    <T extends Model> void save(T model);

    /**
     * 保存数据。
     *
     * @param tableName 表名称。
     * @param id        数据ID值。
     * @param map       属性集，key为属性名称，value为属性值。
     */
    void save(String tableName, String id, Map<String, Object> map);

    /**
     * 删除数据。
     *
     * @param model Model对象。
     * @param <T>   Model类型。
     */
    <T extends Model> void delete(T model);

    /**
     * 删除数据。
     *
     * @param tableName 表名称。
     * @param id        数据ID值。
     */
    void delete(String tableName, String id);
}
