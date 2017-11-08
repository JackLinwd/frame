package org.lwd.frame.bean;

/**
 * Bean环境关闭监听器。
 *
 * @author lwd
 */
public interface ContextClosedListener {
    /**
     * 获取执行顺序，值越小则越先执行。
     *
     * @return 执行顺序。
     */
    int getContextClosedSort();

    /**
     * 当Bean环境关闭时，自动执行此方法。
     */
    void onContextClosed();
}
