package org.lwd.frame.dao;

import com.alibaba.fastjson.JSONObject;

/**
 * DAO连接工厂。
 * 定义DAO连接工厂接口。
 *
 * @author lwd
 */
public interface ConnectionFactory<T> {
    /**
     * 获取可写数据连接。
     *
     * @param name 数据源引用名称。
     * @return 可写数据连接；如果不存在则返回null。
     */
    T getWriteable(String name);

    /**
     * 获取只读数据连接。
     *
     * @param name 数据源引用名称。
     * @return 只读数据连接；如果不存在则返回null。
     */
    T getReadonly(String name);

    /**
     * 创建数据连接。
     *
     * @param config 数据连接配置。
     */
    void create(JSONObject config);
}
