package org.lwd.frame.dao.model;

/**
 * 持久化模型定义。
 *
 * @author lwd
 */
public interface Model {
    /**
     * 获得Model ID值。
     *
     * @return Model ID值。
     */
    String getId();

    /**
     * 设置Model ID值。
     *
     * @param id Model ID值。
     */
    void setId(String id);
}
