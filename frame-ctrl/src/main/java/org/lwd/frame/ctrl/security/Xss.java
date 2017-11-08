package org.lwd.frame.ctrl.security;

import java.util.Map;

/**
 * XSS安全验证。
 *
 * @author lwd
 */
public interface Xss {
    /**
     * 验证是否包含XSS跨站脚本攻击风险。
     *
     * @param uri URI地址。
     * @param map 参数集。
     * @return 如果包含则返回true；否则返回false。
     */
    boolean contains(String uri, Map<String, String> map);
}
