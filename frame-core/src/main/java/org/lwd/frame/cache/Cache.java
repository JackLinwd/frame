package org.lwd.frame.cache;

/**
 * 缓存管理器。
 *
 * @author lwd
 */
public interface Cache {
    /**
     * 默认类型。
     */
    String TYPE_DEFAULT = null;
    /**
     * Redis缓存。
     */
    String TYPE_REDIS = "redis";

    /**
     * 保存缓存对象。
     *
     * @param key      引用key；如果为空则不缓存。
     * @param value    缓存对象；如果为null则不缓存。
     * @param resident 是否常驻内存，如果是则不被自动清除；否则将根据清除规则被清除。
     */
    void put(String key, Object value, boolean resident);

    /**
     * 保存缓存对象。
     *
     * @param type     缓存类型，为空则使用默认缓存。
     * @param key      引用key；如果为空则不缓存。
     * @param value    缓存对象；如果为null则不缓存。
     * @param resident 是否常驻内存，如果是则不被自动清除；否则将根据清除规则被清除。
     */
    void put(String type, String key, Object value, boolean resident);

    /**
     * 获取缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T get(String key);

    /**
     * 获取缓存对象。
     *
     * @param type 缓存类型，为空则使用默认缓存。
     * @param key  引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T get(String type, String key);

    /**
     * 移除缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T remove(String key);

    /**
     * 移除缓存对象。
     *
     * @param type 缓存类型，为空则使用默认缓存。
     * @param key  引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T remove(String type, String key);
}
