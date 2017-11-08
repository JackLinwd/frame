package org.lwd.frame.atomic;

/**
 * @author lwd
 */
public interface Closables {
    /**
     * 关闭所有可关闭事务。
     */
    void close();
}
