package org.lwd.frame.ctrl.http.upload;

import org.lwd.frame.bean.BeanFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author lwd
 */
@WebServlet(name = "UploadPathServlet", urlPatterns = {UploadHelper.UPLOAD_PATH})
public class UploadPathServlet extends HttpServlet {
    private UploadHelper uploadHelper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        uploadHelper = BeanFactory.getBean(UploadHelper.class);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        uploadHelper.upload(request, response, UploadHelper.UPLOAD_PATH);
    }
}