package org.lwd.frame.ctrl.http.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lwd
 */
public interface UploadHelper {
    /**
     * 前缀。
     */
    String PREFIX = "frame.ctrl.http.upload.";
    /**
     * 上传Servlet URI地址。
     */
    String URI = "/frame/ctrl-http/upload";

    /**
     * 上传文件。
     *
     * @param request  请求HttpServletRequest信息。
     * @param response 输出HttpServletResponse信息。
     */
    void upload(HttpServletRequest request, HttpServletResponse response);
}
