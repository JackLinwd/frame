package org.lwd.frame.ctrl.http.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie织入器。
 *
 * @author lwd
 */
public interface CookieAware {
    /**
     * 设置Cookie环境。
     *
     * @param request  请求HttpServletRequest对象。
     * @param response 输出HttpServletResponse对象。
     */
    public void set(HttpServletRequest request, HttpServletResponse response);
}
