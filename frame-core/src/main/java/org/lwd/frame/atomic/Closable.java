package org.lwd.frame.atomic;

/**
 * 可关闭事务。
 *
 * @author lwd
 */
public interface Closable {
    /**
     * 关闭。
     */
    void close();
}
