package org.lwd.frame.bean;

/**
 * 类重载器。
 *
 * @author lwd
 */
public interface ClassReloader {
    /**
     * 验证类名是否需要重新载入。
     *
     * @param name 要验证的类名。
     * @return 如果需要重新载入则返回true；否则返回false。
     */
    boolean isReloadEnable(String name);

    /**
     * 获得重新载入的类根路径。
     *
     * @return 重新载入的类根路径。
     */
    String getClassPath();
}
