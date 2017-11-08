package org.lwd.frame.script;

import java.util.Map;

/**
 * 脚本请求参数集。
 *
 * @author lwd
 */
public interface Arguments {
    /**
     * 获取请求参数。
     *
     * @param name 参数名。
     * @return 参数值；如果不存在则返回null。
     */
    Object get(String name);

    /**
     * 设置参数。
     *
     * @param name  参数名。
     * @param value 参数值。
     */
    void set(String name, Object value);

    /**
     * 获取所有参数。
     *
     * @return 参数集；如果不存在则返回空集。
     */
    Map<String, Object> all();
}
