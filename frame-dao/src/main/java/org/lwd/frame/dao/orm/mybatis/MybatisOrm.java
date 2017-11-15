package org.lwd.frame.dao.orm.mybatis;

import org.lwd.frame.atomic.Atomicable;
import org.lwd.frame.dao.Mode;

import java.util.List;
import java.util.Map;

/**
 * MybatisOrm。主要提供基于MyBatis的ORM支持。
 *
 * @author lwd
 */
public interface MybatisOrm extends Atomicable {
    /**
     * 获取Mapper实例。
     *
     * @param dataSource  数据源，如果为null则使用默认数据源。
     * @param mode        连接类型，如果为null则使用可读写连接。
     * @param mapperClass Mapper类。
     * @param <T>         Mapper类定义。
     * @return Mapper实例。
     */
    <T> T getMapper(String dataSource, Mode mode, Class<T> mapperClass);

    /**
     * 检索一个数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @param <T>     返回结果类型。
     * @return 返回结果。
     */
    <T> T selectOne(MybatisBuilder builder);

    /**
     * 检索数据集。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @param <T>     返回数据类定义。
     * @return 数据集。
     */
    <T> List<T> selectList(MybatisBuilder builder);

    /**
     * 检索Map数据集。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @param <K>     返回数据key类定义。
     * @param <V>     返回数据value类定义。
     * @return Map数据集。
     */
    <K, V> Map<K, V> selectMap(MybatisBuilder builder);

    /**
     * 执行检索，并将检索结果交由ResultHandler处理。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     */
    void select(MybatisBuilder builder);

    /**
     * 插入数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @return 受影响记录数。
     */
    int insert(MybatisBuilder builder);

    /**
     * 更新数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @return 受影响记录数。
     */
    int update(MybatisBuilder builder);

    /**
     * 删除数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @return 受影响记录数。
     */
    int delete(MybatisBuilder builder);
}
