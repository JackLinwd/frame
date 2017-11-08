package org.lwd.frame.cache.lr;

/**
 * 本地缓存。
 *
 * @author lwd
 */
public interface Local {
    /**
     * 保存缓存对象。
     *
     * @param element 缓存对象。
     */
    void put(Element element);

    /**
     * 获得缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    Element get(String key);

    /**
     * 移除缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    Element remove(String key);
}
