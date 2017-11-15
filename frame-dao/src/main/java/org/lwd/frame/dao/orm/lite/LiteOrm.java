package org.lwd.frame.dao.orm.lite;

import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.orm.Orm;

/**
 * 简单ORM。主要提供高效的ORM，但不提供自动外联合映射的功能。
 *
 * @author lwd
 */
public interface LiteOrm extends Orm<LiteQuery> {
    /**
     * 重置内存表数据。
     * 此过程会先删除内存表现有数据再导入磁盘表数据到内存表中。
     *
     * @param modelClass Model类。
     */
    void resetMemory(Class<? extends Model> modelClass);

    /**
     * 重置内存表数据。
     * 此过程会先删除内存表现有数据再导入磁盘表数据到内存表中。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param modelClass Model类。
     */
    void resetMemory(String dataSource, Class<? extends Model> modelClass);
}
