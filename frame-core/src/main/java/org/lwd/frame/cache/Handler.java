package org.lwd.frame.cache;

/**
 * 缓存处理器。
 *
 * @author lwd
 */
public interface Handler {
    /**
     * 获取处理器名称。
     *
     * @return 处理器名称。
     */
    String getName();

    /**
     * 保存缓存对象。
     *
     * @param key      引用key；如果为空则不缓存。
     * @param value    缓存对象；如果为null则不缓存。
     * @param resident 是否常驻内存，如果是则不被自动清除；否则将根据清除规则被清除。
     */
    void put(String key, Object value, boolean resident);

    /**
     * 获取缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T get(String key);

    /**
     * 移除缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T remove(String key);
}
