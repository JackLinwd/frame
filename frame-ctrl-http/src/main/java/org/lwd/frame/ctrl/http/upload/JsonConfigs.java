package org.lwd.frame.ctrl.http.upload;

/**
 * @author lwd
 */
public interface JsonConfigs {
    /**
     * 获取JsonConfig实例。
     *
     * @param key 配置key。
     * @return JsonConfig实例；如果不存在则返回null。
     */
    JsonConfig get(String key);
}
