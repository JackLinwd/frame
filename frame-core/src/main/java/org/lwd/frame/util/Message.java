package org.lwd.frame.util;

/**
 * 资源。用于获取项目中message.properties中的资源。
 *
 * @author lwd
 */
public interface Message {
    /**
     * 获得资源。
     *
     * @param key  资源key。
     * @param args 资源参数集。
     * @return 资源。
     */
    String get(String key, Object... args);

    /**
     * 获取资源，并根据逗号分割为数组。
     *
     * @param key  资源key。
     * @param args 资源参数集。
     * @return 资源。
     */
    String[] getAsArray(String key, Object... args);
}
