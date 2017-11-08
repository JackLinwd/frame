package org.lwd.frame.freemarker;


import org.lwd.frame.util.Converter;
import org.lwd.frame.util.DateTime;
import org.lwd.frame.util.Message;

/**
 * @author lwd
 */
public interface Model {
    /**
     * 获取转换器。
     *
     * @return 转换器。
     */
    Converter getConverter();

    /**
     * 获取资源信息管理器。
     *
     * @return 资源信息管理器。
     */
    Message getMessage();

    /**
     * 获取日期时间处理器。
     *
     * @return 日期时间处理器。
     */
    DateTime getDatetime();

    /**
     * 获取数据实例。
     *
     * @return 数据实例。
     */
    Object getData();

    /**
     * 设置数据实例。
     *
     * @param data 数据实例。
     * @return 当前Model实例。
     */
    Model setData(Object data);
}
