package org.lwd.frame.atomic;

/**
 * 可失败事务。
 *
 * @author lwd
 */
public interface Failable {
    /**
     * 失败。
     *
     * @param throwable 异常。
     */
    void fail(Throwable throwable);
}
