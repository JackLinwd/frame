package org.lwd.frame.ctrl;

/**
 * 权限验证器。
 *
 * @author lwd
 */
public interface Permit {
    /**
     * 验证权限。
     *
     * @return 如果允许访问则返回true；否则返回false。
     */
    boolean allow();
}
