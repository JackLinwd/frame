package org.lwd.frame.ctrl.http.context;

import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.ctrl.context.ResponseAdapter;
import org.lwd.frame.util.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
public class ResponseAdapterImpl implements ResponseAdapter {
    private String servletContextPath;
    private HttpServletResponse response;
    private OutputStream output;

    public ResponseAdapterImpl(String servletContextPath, HttpServletResponse response, OutputStream output) {
        this.servletContextPath = servletContextPath;
        this.response = response;
        this.output = output;
    }

    @Override
    public void setContentType(String contentType) {
        response.setContentType(contentType);
    }

    @Override
    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    @Override
    public OutputStream getOutputStream() {
        return output;
    }

    @Override
    public void send() throws IOException {
        output.close();
    }

    @Override
    public void redirectTo(String url) {
        try {
            response.sendRedirect(url.indexOf('/') == 0 ? (servletContextPath + url) : url);
            output.close();
        } catch (IOException e) {
            BeanFactory.getBean(Logger.class).warn(e, "跳转到远程URL[{}]地址时发生异常！", url);
        }
    }

    @Override
    public void sendError(int code) {
        try {
            response.sendError(code);
        } catch (IOException e) {
            BeanFactory.getBean(Logger.class).warn(e, "发送错误码[{}]时发生异常！", code);
        }
    }
}
