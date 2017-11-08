package org.lwd.frame.ctrl.http.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lwd
 */
public interface UploadHelper {
    /**
     * 上传Servlet URI地址。
     */
    String URI = "/frame/ctrl-http/upload";
    /**
     * 上传文件保存根路径。
     */
    String ROOT = "/upload/";

    /**
     * 上传文件。
     *
     * @param request  请求HttpServletRequest信息。
     * @param response 输出HttpServletResponse信息。
     */
    void upload(HttpServletRequest request, HttpServletResponse response);

    /**
     * 删除上传的文件。
     *
     * @param key 上传key。
     * @param uri 文件URI地址。
     */
    void remove(String key, String uri);
}
