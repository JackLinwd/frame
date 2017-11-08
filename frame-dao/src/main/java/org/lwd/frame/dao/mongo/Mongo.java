package org.lwd.frame.dao.mongo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.lwd.frame.dao.model.Model;

/**
 * MongoDB操作接口。
 *
 * @author lwd
 */
public interface Mongo {
    /**
     * 获取Mongo数据库。
     *
     * @return Mongo数据库；如果不存在则返回null。
     */
    MongoDatabase getDatabase();

    /**
     * 获取Mongo数据库。
     *
     * @param key 配置key。
     * @return Mongo数据库；如果不存在则返回null。
     */
    MongoDatabase getDatabase(String key);

    /**
     * 获取Mongo文档集。
     *
     * @param modelClass 类名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(Class<? extends Model> modelClass);

    /**
     * 获取Mongo文档集。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(String key, Class<? extends Model> modelClass);

    /**
     * 获取Mongo文档集。
     *
     * @param name 文档名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(String name);

    /**
     * 获取Mongo文档集。
     *
     * @param key  配置key。
     * @param name 文档名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(String key, String name);

    /**
     * 插入一个数据。
     *
     * @param modelClass 类名。
     * @param object     要保存的数据。
     */
    void insert(Class<? extends Model> modelClass, JSONObject object);

    /**
     * 插入一个数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param object     要保存的数据。
     */
    void insert(String key, Class<? extends Model> modelClass, JSONObject object);

    /**
     * 插入一个数据。
     *
     * @param collection 文档名。
     * @param object     要保存的数据。
     */
    void insert(String collection, JSONObject object);

    /**
     * 插入一个数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param object     要保存的数据。
     */
    void insert(String key, String collection, JSONObject object);

    /**
     * 插入多个数据。
     *
     * @param modelClass 类名。
     * @param array      要保存的数据。
     */
    void insert(Class<? extends Model> modelClass, JSONArray array);

    /**
     * 插入多个数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param array      要保存的数据。
     */
    void insert(String key, Class<? extends Model> modelClass, JSONArray array);

    /**
     * 插入多个数据。
     *
     * @param collection 文档名。
     * @param array      要保存的数据。
     */
    void insert(String collection, JSONArray array);

    /**
     * 插入多个数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param array      要保存的数据。
     */
    void insert(String key, String collection, JSONArray array);

    /**
     * 更新数据。
     *
     * @param modelClass 类名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(Class<? extends Model> modelClass, JSONObject set, JSONObject where);

    /**
     * 更新数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(String key, Class<? extends Model> modelClass, JSONObject set, JSONObject where);

    /**
     * 更新数据。
     *
     * @param collection 文档名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(String collection, JSONObject set, JSONObject where);

    /**
     * 更新数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(String key, String collection, JSONObject set, JSONObject where);

    /**
     * 删除数据。
     *
     * @param modelClass 类名。
     * @param where      条件。
     */
    void delete(Class<? extends Model> modelClass, JSONObject where);

    /**
     * 删除数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param where      条件。
     */
    void delete(String key, Class<? extends Model> modelClass, JSONObject where);

    /**
     * 删除数据。
     *
     * @param collection 文档名。
     * @param where      条件。
     */
    void delete(String collection, JSONObject where);

    /**
     * 删除数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param where      条件。
     */
    void delete(String key, String collection, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回null。
     */
    JSONObject findOne(Class<? extends Model> modelClass, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回null。
     */
    JSONObject findOne(String key, Class<? extends Model> modelClass, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回{}。
     */
    JSONObject findOne(String collection, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回{}。
     */
    JSONObject findOne(String key, String collection, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(Class<? extends Model> modelClass, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(String key, Class<? extends Model> modelClass, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(String collection, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(String key, String collection, JSONObject where);

    /**
     * 创建连接。
     *
     * @param config 配置。
     */
    void create(JSONObject config);
}
