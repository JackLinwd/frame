package org.lwd.frame.dao.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.model.ModelTables;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Repository("frame.dao.mongo")
public class MongoImpl implements Mongo, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;
    @Value("${frame.dao.database.max-active:5}")
    private int maxActive;
    @Value("${frame.dao.database.max-wait:5000}")
    private int maxWait;
    @Value("${frame.dao.mongo.config:}")
    private String config;
    @Value("${frame.dao.mongo.key:}")
    private String key;
    private Map<String, String> schemas;
    private Map<String, List<MongoClient>> mongos;

    @Override
    public MongoDatabase getDatabase() {
        return getDatabase(null);
    }

    @Override
    public MongoDatabase getDatabase(String key) {
        if (key == null)
            key = this.key;

        String schema = schemas.get(key);
        if (validator.isEmpty(schema)) {
            logger.warn(null, "MongoDB引用key[{}]不存在！", key);

            throw new NullPointerException("MongoDB引用key[" + key + "]不存在！");
        }

        List<MongoClient> list = mongos.get(key);

        return list.get(generator.random(0, list.size() - 1)).getDatabase(schema);
    }

    @Override
    public MongoCollection<Document> getCollection(Class<? extends Model> modelClass) {
        return getCollection(null, getCollectionName(modelClass));
    }

    @Override
    public MongoCollection<Document> getCollection(String key, Class<? extends Model> modelClass) {
        return getCollection(key, getCollectionName(modelClass));
    }

    @Override
    public MongoCollection<Document> getCollection(String name) {
        return getCollection(null, name);
    }

    @Override
    public MongoCollection<Document> getCollection(String key, String name) {
        MongoDatabase database = getDatabase(key);

        return database == null ? null : database.getCollection(name);
    }

    @Override
    public void insert(Class<? extends Model> modelClass, JSONObject object) {
        insert(null, getCollectionName(modelClass), object);
    }

    @Override
    public void insert(String key, Class<? extends Model> modelClass, JSONObject object) {
        insert(key, getCollectionName(modelClass), object);
    }

    @Override
    public void insert(String collection, JSONObject object) {
        insert(null, collection, object);
    }

    @Override
    public void insert(String key, String collection, JSONObject object) {
        MongoCollection<Document> mc = getCollection(key, collection);
        if (mc == null)
            return;

        getCollection(key, collection).insertOne(toDocument(object));
    }

    @Override
    public void insert(Class<? extends Model> modelClass, JSONArray array) {
        insert(null, getCollectionName(modelClass), array);
    }

    @Override
    public void insert(String key, Class<? extends Model> modelClass, JSONArray array) {
        insert(key, getCollectionName(modelClass), array);
    }

    @Override
    public void insert(String collection, JSONArray array) {
        insert(null, collection, array);
    }

    @Override
    public void insert(String key, String collection, JSONArray array) {
        MongoCollection<Document> mc = getCollection(key, collection);
        if (mc == null)
            return;

        for (int i = 0; i < array.size(); i++)
            mc.insertOne(toDocument(array.getJSONObject(i)));
    }

    @Override
    public void update(Class<? extends Model> modelClass, JSONObject set, JSONObject where) {
        update(null, getCollectionName(modelClass), set, where);
    }

    @Override
    public void update(String key, Class<? extends Model> modelClass, JSONObject set, JSONObject where) {
        update(key, getCollectionName(modelClass), set, where);
    }

    @Override
    public void update(String collection, JSONObject set, JSONObject where) {
        update(null, collection, set, where);
    }

    @Override
    public void update(String key, String collection, JSONObject set, JSONObject where) {
        MongoCollection<Document> mc = getCollection(key, collection);
        if (mc == null)
            return;

        mc.updateMany(toDocument(where), new Document("$set", toDocument(set)));
    }

    @Override
    public void delete(Class<? extends Model> modelClass, JSONObject where) {
        delete(null, getCollectionName(modelClass), where);
    }

    @Override
    public void delete(String key, Class<? extends Model> modelClass, JSONObject where) {
        delete(key, getCollectionName(modelClass), where);
    }

    @Override
    public void delete(String collection, JSONObject where) {
        delete(null, collection, where);
    }

    @Override
    public void delete(String key, String collection, JSONObject where) {
        MongoCollection<Document> mc = getCollection(key, collection);
        if (mc == null)
            return;

        mc.deleteMany(toDocument(where));
    }

    @Override
    public JSONObject findOne(Class<? extends Model> modelClass, JSONObject where) {
        return findOne(null, getCollectionName(modelClass), where);
    }

    @Override
    public JSONObject findOne(String key, Class<? extends Model> modelClass, JSONObject where) {
        return findOne(key, getCollectionName(modelClass), where);
    }

    @Override
    public JSONObject findOne(String collection, JSONObject where) {
        return findOne(null, collection, where);
    }

    @Override
    public JSONObject findOne(String key, String collection, JSONObject where) {
        MongoCollection<Document> mc = getCollection(key, collection);
        if (mc == null)
            return new JSONObject();

        Document document = mc.find(toDocument(where)).first();

        return document == null || document.isEmpty() ? new JSONObject() : JSON.parseObject(document.toJson());
    }

    @Override
    public JSONArray find(Class<? extends Model> modelClass, JSONObject where) {
        return find(null, getCollectionName(modelClass), where);
    }

    @Override
    public JSONArray find(String key, Class<? extends Model> modelClass, JSONObject where) {
        return find(key, getCollectionName(modelClass), where);
    }

    @Override
    public JSONArray find(String collection, JSONObject where) {
        return find(null, collection, where);
    }

    @Override
    public JSONArray find(String key, String collection, JSONObject where) {
        MongoCollection<Document> mc = getCollection(key, collection);
        if (mc == null)
            return new JSONArray();

        JSONArray array = new JSONArray();
        for (Document document : mc.find(toDocument(where)))
            array.add(JSON.parseObject(document.toJson()));

        return array;
    }

    private String getCollectionName(Class<? extends Model> modelClass) {
        return modelTables.get(modelClass).getTableName();
    }

    private Document toDocument(JSONObject object) {
        return object == null ? new Document() : new Document(object);
    }

    @Override
    public void create(JSONObject config) {
        String key = config.getString("key");
        if (mongos.containsKey(key))
            return;

        String schema = config.getString("schema");
        if (validator.isEmpty(schema))
            throw new NullPointerException("未设置schema值[" + config + "]！");

        JSONArray array = config.getJSONArray("ips");
        if (array == null || array.size() == 0)
            throw new NullPointerException("未设置ips值[" + config + "]！");

        String username = config.getString("username");
        String password = config.getString("password");
        MongoClientOptions.Builder builder = MongoClientOptions.builder().connectionsPerHost(maxActive).maxWaitTime(maxWait);
        List<MongoClient> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++)
            list.add(new MongoClient(new MongoClientURI("mongodb://" + username + ":" + password + "@" + array.getString(i) + "/" + schema, builder)));
        schemas.put(key, schema);
        mongos.put(key, list);

        if (logger.isDebugEnable())
            logger.debug("Mongo数据库[{}]初始化完成。", config);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(config) || schemas != null || mongos != null)
            return;

        schemas = new HashMap<>();
        mongos = new HashMap<>();
        JSONArray array = JSON.parseArray(config);
        if (array != null && array.size() > 0)
            for (int i = 0; i < array.size(); i++)
                create(array.getJSONObject(i));
    }
}
