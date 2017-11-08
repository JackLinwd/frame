package org.lwd.frame.dao.dialect;

/**
 * 方言工厂。
 *
 * @author lwd
 */
public interface DialectFactory {
    /**
     * 获取方言实例。
     *
     * @param name 方言名称。
     * @return 方言实例。
     */
    Dialect get(String name);
}
