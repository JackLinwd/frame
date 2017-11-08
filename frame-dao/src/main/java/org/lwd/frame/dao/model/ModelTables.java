package org.lwd.frame.dao.model;

import java.util.Set;

/**
 * Model-Table集。用于保存所有Model-Table映射关系表，并提供查询。
 *
 * @author lwd
 */
public interface ModelTables {
    /**
     * 获取Model类对应的Model-Table映射关系表。
     *
     * @param modelClass Model类。
     * @return Model-Table映射关系表。
     */
    ModelTable get(Class<? extends Model> modelClass);

    /**
     * 获取所有Model类定义。
     *
     * @return Model类定义集。
     */
    Set<Class<? extends Model>> getModelClasses();
}
