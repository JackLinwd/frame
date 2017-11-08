package org.lwd.frame.cache.lr;

/**
 * 远程缓存。
 *
 * @author lpw
 */
public interface Remote {
    /**
     * 获得唯一ID值，此ID值可用于区分网络上的其他节点。
     *
     * @return 唯一ID值。
     */
    String getId();

    /**
     * 保存缓存对象。
     *
     * @param element 缓存对象。
     */
    void put(Element element);

    /**
     * 移除缓存对象。
     *
     * @param key 引用key。
     */
    void remove(String key);
}
