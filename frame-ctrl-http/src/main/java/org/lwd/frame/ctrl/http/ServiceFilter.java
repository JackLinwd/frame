package org.lwd.frame.ctrl.http;

import org.lwd.frame.bean.BeanFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lwd
 */
public class ServiceFilter implements Filter {
    protected ServiceHelper helper;

    @Override
    public void init(FilterConfig config) throws ServletException {
        helper = BeanFactory.getBean(ServiceHelper.class);
        helper.setPath(config.getServletContext().getRealPath(""), config.getServletContext().getContextPath());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!helper.service((HttpServletRequest) request, (HttpServletResponse) response))
            chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
