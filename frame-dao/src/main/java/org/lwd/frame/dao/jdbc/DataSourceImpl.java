package org.lwd.frame.dao.jdbc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.dao.dialect.Dialect;
import org.lwd.frame.dao.dialect.DialectFactory;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwd
 */
@Repository("frame.dao.jdbc.data-source")
public class DataSourceImpl implements org.lwd.frame.dao.jdbc.DataSource, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;
    @Inject
    private DialectFactory dialectFactory;
    @Value("${frame.dao.database.initial-size:0}")
    private int initialSize;
    @Value("${frame.dao.database.max-active:5}")
    private int maxActive;
    @Value("${frame.dao.database.max-wait:5000}")
    private int maxWait;
    @Value("${frame.dao.database.test-interval:30000}")
    private int testInterval;
    @Value("${frame.dao.database.remove-abandoned-timeout:60}")
    private int removeAbandonedTimeout;
    @Value("${frame.dao.database.config:}")
    private String config;
    @Value("${frame.dao.data-source.key:}")
    private String key;
    private Map<String, JSONObject> configs = new HashMap<>();
    private Map<String, Dialect> dialects = new HashMap<>();
    private Map<String, DataSource> writeables = new ConcurrentHashMap<>();
    private Map<String, List<DataSource>> readonlys = new ConcurrentHashMap<>();
    private Map<String, Boolean> readonly = new ConcurrentHashMap<>();

    @Override
    public DataSource getWriteable(String key) {
        return writeables.get(getKey(key));
    }

    @Override
    public DataSource getReadonly(String key) {
        if (!hasReadonly(key))
            return getWriteable(key);

        List<DataSource> list = listReadonly(key);

        return list.get(generator.random(0, list.size() - 1));
    }

    @Override
    public List<DataSource> listReadonly(String key) {
        return readonlys.get(getKey(key));
    }

    @Override
    public boolean hasReadonly(String key) {
        return readonly.getOrDefault(getKey(key), false);
    }

    @Override
    public Map<String, Dialect> getDialects() {
        return dialects;
    }

    @Override
    public Dialect getDialect(String key) {
        return dialects.get(getKey(key));
    }

    @Override
    public String getKey(String key) {
        return key == null ? this.key : key;
    }

    @Override
    public String getDefaultKey() {
        return key;
    }

    @Override
    public JSONObject getConfig(String key) {
        return configs.get(key);
    }

    @Override
    public int getContextRefreshedSort() {
        return 3;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(config))
            return;

        JSONArray array = JSON.parseArray(config);
        for (int i = 0; i < array.size(); i++)
            create(array.getJSONObject(i));
    }

    @Override
    public synchronized void create(JSONObject config) {
        String key = config.getString("key");
        configs.put(key, config);
        Dialect dialect = dialectFactory.get(config.getString("type"));
        dialects.put(key, dialect);
        createDataSource(key, dialect, config.getString("username"), config.getString("password"), config.getJSONArray("ips"), config.getString("schema"));

        if (logger.isInfoEnable())
            logger.info("成功创建数据库[{}]连接池。", config);
    }

    private void createDataSource(String key, Dialect dialect, String username, String password, JSONArray ips, String schema) {
        if (key == null || writeables.get(key) != null)
            return;

        for (int i = 0; i < ips.size(); i++) {
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
            dataSource.setDriverClassName(dialect.getDriver());
            dataSource.setUrl(dialect.getUrl(ips.getString(i), schema));
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setInitialSize(initialSize);
            dataSource.setMaxActive(maxActive);
            dataSource.setMaxIdle(maxActive);
            dataSource.setMinIdle(initialSize);
            dataSource.setMaxWait(maxWait);
            dataSource.setTestWhileIdle(false);
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(dialect.getValidationQuery());
            dataSource.setTestOnReturn(false);
            dataSource.setValidationInterval(testInterval);
            dataSource.setTimeBetweenEvictionRunsMillis(testInterval);
            dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
            dataSource.setMinEvictableIdleTimeMillis(testInterval);
            dataSource.setRemoveAbandoned(true);
            dataSource.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                    "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

            if (i == 0)
                writeables.put(key, dataSource);
            else {
                List<DataSource> list = readonlys.get(key);
                if (list == null)
                    list = Collections.synchronizedList(new ArrayList<>());
                list.add(dataSource);
                readonlys.put(key, list);
                readonly.put(key, true);
            }

            if (logger.isInfoEnable())
                logger.info("数据源[{}@{}]设置完成。", key, ips.getString(i));
        }
    }
}