package org.lwd.frame.ctrl.context;

/**
 * @author lwd
 */
public interface Session {
    /**
     * 设置Session值。
     *
     * @param key   引用key。
     * @param value 值。
     */
    void set(String key, Object value);

    /**
     * 设置Session值。
     *
     * @param id    Session ID。
     * @param key   引用key。
     * @param value 值。
     */
    void set(String id, String key, Object value);

    /**
     * 获得Session值。
     *
     * @param key 引用key。
     * @return 值；如果不存在则返回null。
     */
    <T> T get(String key);

    /**
     * 获得Session值。
     *
     * @param id  Session ID。
     * @param key 引用key。
     * @return 值；如果不存在则返回null。
     */
    <T> T get(String id, String key);

    /**
     * 移除Session值。
     *
     * @param key 引用key。
     */
    void remove(String key);

    /**
     * 移除Session值。
     *
     * @param id  Session ID。
     * @param key 引用key。
     */
    void remove(String id, String key);

    /**
     * 获得Session ID值。
     *
     * @return Session ID值。
     */
    String getId();
}
