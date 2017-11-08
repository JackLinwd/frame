package org.lwd.frame.ctrl.http;

import org.lwd.frame.bean.BeanFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 安全过滤器。
 *
 * @author lwd
 */
public class SecurityFilter implements Filter {
    protected SecurityHelper securityHelper;

    @Override
    public void init(FilterConfig config) throws ServletException {
        securityHelper = BeanFactory.getBean(SecurityHelper.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        filter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    protected void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (securityHelper.isEnable(request))
            chain.doFilter(request, response);
        else
            response.sendError(404);

    }

    @Override
    public void destroy() {
    }
}
