package org.lwd.frame.ctrl.context;


import org.lwd.frame.dao.model.Model;

import java.util.Date;
import java.util.Map;

/**
 * 请求。
 *
 * @author lwd
 */
public interface Request {
    /**
     * 获得请求参数值。
     *
     * @param name 参数名称。
     * @return 参数值。
     */
    String get(String name);

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
     * @param name         参数名称。
     * @param defaultValue 默认值。
     * @return 整型参数值。
     */
    int getAsInt(String name, int defaultValue);

    /**
     * 获得整型请求参数值。
     *
     * @param name 参数名称。
     * @return 整型参数值。
     */
    long getAsLong(String name);

    /**
     * 获得整型请求参数值。
     *
     * @param name         参数名称。
     * @param defaultValue 默认值。
     * @return 整型参数值。
     */
    long getAsLong(String name, long defaultValue);

    /**
     * 获取布尔请求参数值。
     *
     * @param name 参数名。
     * @return 布尔参数值。
     */
    boolean getAsBoolean(String name);

    /**
     * 获得日期型请求参数值。
     *
     * @param name 参数名称。
     * @return 日期型参数值。
     */
    Date getAsDate(String name);

    /**
     * 获得日期型请求参数值。
     *
     * @param name 参数名称。
     * @return 日期型参数值。
     */
    java.sql.Date getAsSqlDate(String name);

    /**
     * 获得字符串数组请求参数值。
     * 多个值间以逗号分隔。
     *
     * @param name 参数名称。
     * @return 字符串数组。
     */
    String[] getAsArray(String name);

    /**
     * 获得所有请求参数值对。
     *
     * @return 请求参数值对。
     */
    Map<String, String> getMap();

    /**
     * 获取InputStream中的数据。
     *
     * @return InputStream中的数据；如果不存在则返回空字符串。
     */
    String getFromInputStream();

    /**
     * 将请求参数集保存到Model实例中。
     *
     * @param model Model实例。
     * @return Model实例。
     */
    <T extends Model> T setToModel(T model);

    /**
     * 获取服务器名。
     *
     * @return 服务器名。
     */
    String getServerName();

    /**
     * 获取服务器端口号。
     *
     * @return 服务器端口号。
     */
    int getServerPort();

    /**
     * 获取部署项目名。
     *
     * @return 部署项目名。
     */
    String getContextPath();

    /**
     * 获取请求URL。
     *
     * @return 请求URL。
     */
    String getUrl();

    /**
     * 获取请求URI。
     *
     * @return 请求URI。
     */
    String getUri();

    /**
     * 获取请求方法。
     *
     * @return 请求方法。
     */
    String getMethod();
}
