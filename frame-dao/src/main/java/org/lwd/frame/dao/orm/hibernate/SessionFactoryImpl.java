package org.lwd.frame.dao.orm.hibernate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hibernate.SessionFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.dao.dialect.Dialect;
import org.lwd.frame.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwd
 */
@Repository("frame.dao.orm.hibernate.session-factory")
public class SessionFactoryImpl implements org.lwd.frame.dao.orm.hibernate.SessionFactory, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private org.lwd.frame.dao.jdbc.DataSource dataSource;
    @Value("${frame.dao.orm.hibernate.show-sql:false}")
    private boolean showSql;
    @Value("${frame.dao.orm.hibernate.packages-to-scan:}")
    private String packagesToScan;
    private Map<String, SessionFactory> writeables = new ConcurrentHashMap<>();
    private Map<String, List<SessionFactory>> readonlys = new ConcurrentHashMap<>();

    @Override
    public SessionFactory getWriteable(String dataSource) {
        return writeables.get(dataSource);
    }

    @Override
    public SessionFactory getReadonly(String dataSource) {
        if (!this.dataSource.hasReadonly(dataSource))
            return getWriteable(dataSource);

        List<SessionFactory> list = readonlys.get(dataSource);

        return list.get(generator.random(0, list.size() - 1));
    }

    @Override
    public synchronized void create(JSONObject config) {
        create(dataSource.getDialects(), config);
    }

    private void create(Map<String, Dialect> dialects, JSONObject config) {
        String key = config.getString("key");
        createSessionFactory(key, createProperties(dialects.get(key)), json.getAsStringArray(config, "values"));
    }

    private Properties createProperties(Dialect dialect) {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect.getHibernateDialect());
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.cache.use_second_level_cache", false);
        properties.put("hibernate.current_session_context_class", "thread");

        return properties;
    }

    private void createSessionFactory(String name, Properties properties, String[] packagesToScan) {
        if (writeables.get(name) != null)
            return;

        writeables.put(name, createSessionFactory(properties, dataSource.getWriteable(name), packagesToScan));
        if (dataSource.hasReadonly(name)) {
            List<SessionFactory> list = new ArrayList<>();
            dataSource.listReadonly(name).forEach(dataSource -> list.add(createSessionFactory(properties, dataSource, packagesToScan)));
            readonlys.put(name, list);
        }

        if (logger.isInfoEnable())
            logger.info("Hibernate环境[{}@{}]初始化完成。", name, converter.toString(packagesToScan));
    }

    private SessionFactory createSessionFactory(Properties properties, DataSource dataSource, String[] packagesToScan) {
        if (dataSource == null)
            throw new NullPointerException("数据源不存在，无法初始化Hibernate环境！");

        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setHibernateProperties(properties);
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan(packagesToScan);
        try {
            sessionFactoryBean.afterPropertiesSet();

            return sessionFactoryBean.getObject();
        } catch (IOException e) {
            logger.warn(e, "初始化Hibernate环境[{}]时发生异常！", converter.toString(packagesToScan));

            return null;
        }
    }

    @Override
    public int getContextRefreshedSort() {
        return 4;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(packagesToScan))
            return;

        Map<String, Dialect> dialects = dataSource.getDialects();
        JSONArray array = JSON.parseArray(packagesToScan);
        if (array.size() == 0)
            return;

        for (int i = 0; i < array.size(); i++)
            create(dialects, array.getJSONObject(i));
    }
}
