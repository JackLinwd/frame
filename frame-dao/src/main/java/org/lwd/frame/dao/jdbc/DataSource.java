package org.lwd.frame.dao.jdbc;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.dao.ConnectionFactory;
import org.lwd.frame.dao.dialect.Dialect;

import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
public interface DataSource extends ConnectionFactory<javax.sql.DataSource> {
    /**
     * 获取只读数据源集。
     *
     * @param key 数据源引用名称。
     * @return 只读数据源集；如果不存在则返回null。
     */
    List<javax.sql.DataSource> listReadonly(String key);

    /**
     * 验证是否包含只读数据源。
     *
     * @param key 数据源引用名称。
     * @return 如果包含只读数据源则返回true；否则返回false。
     */
    boolean hasReadonly(String key);

    /**
     * 获取数据库方言。
     *
     * @return 数据库方言。
     */
    Map<String, Dialect> getDialects();

    /**
     * 获取配置所使用的方言。
     *
     * @param key 配置key。
     * @return 方言实例。
     */
    Dialect getDialect(String key);

    /**
     * 获取数据源名称。
     *
     * @param key 数据源名称。
     * @return 数据源名称。
     */
    String getKey(String key);

    /**
     * 获取默认数据源key值。
     *
     * @return 数据源key值。
     */
    String getDefaultKey();

    /**
     * 获取数据源配置。
     *
     * @param key 数据源key值。
     * @return 数据源配置；如果不存在则返回null。
     */
    JSONObject getConfig(String key);
}
