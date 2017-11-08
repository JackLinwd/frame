package org.lwd.frame.dao;

import org.lwd.frame.atomic.Atomicable;

/**
 * DAO连接。
 * 定义DAO连接接口。
 *
 * @author lwd
 */
public interface Connection<T> extends Atomicable {
    /**
     * 开始事务控制。
     * 开始后当前线程通过get方法请求的Connection均为同一个，并且为可读写连接实例。
     * 也可以通过在方法上添加@javax.transaction.Transactional注解来开启。
     * 事务会在rollback或close方法被调用时，自动提交并结束。
     */
    void beginTransaction();

    /**
     * 获取一个数据连接。返回线程安全的数据连接，即每个线程使用独立的数据连接。
     *
     * @param dataSource 数据源名称，为空则使用默认数据源。
     * @param mode       数据操作方式 ；为null则获取可读写数据源。
     * @return 数据连接；如果获取失败则返回null。
     */
    T get(String dataSource, Mode mode);
}
