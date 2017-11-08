package org.lwd.frame.ctrl.http.context;

import java.util.Date;

/**
 * Cookie管理。
 *
 * @author lwd
 */
public interface Cookie {
    /**
     * 添加信息到Cookie中。
     *
     * @param name   Cookie名称。
     * @param value  Cookie数据。
     * @param path   有效路径URI地址。如果为空则仅当前路径及其子路径可以访问，如果设置为“/”则所有路径均可访问。
     * @param expiry 有效时间，单位：秒。如果小于等于0则不设置，即随浏览器的关闭而失效。
     */
    void add(String name, String value, String path, int expiry);

    /**
     * 获取Cookie中保存的数据。
     * 如果包含多个数据，则返回最后/新的一个。
     * 如果不存在则返回null。
     *
     * @param name Cookie名称。
     * @return Cookie中保存的数据。
     */
    String get(String name);

    /**
     * 获取Cookie中保存的数据集。
     * 如果不存在则返回空集。
     *
     * @param name Cookie名称。
     * @return Cookie中保存的数据集。
     */
    String[] getAll(String name);

    /**
     * 获得整型请求参数值。
     *
     * @param name 参数名称。
     * @return 整型参数值。
     */
    int getAsInt(String name);

    /**
     * 获得整型请求参数值。
     *
     * @param name 参数名称。
     * @return 整型参数值。
     */
    long getAsLong(String name);

    /**
     * 获得日期型请求参数值。
     *
     * @param name 参数名称。
     * @return 日期型参数值。
     */
    Date getAsDate(String name);
}
