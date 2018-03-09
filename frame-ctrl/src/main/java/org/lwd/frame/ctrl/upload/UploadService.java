package org.lwd.frame.ctrl.upload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author lwd
 */
public interface UploadService {
    /**
     * 前缀。
     */
    String PREFIX = "frame.ctrl.upload.";

    /**
     * 上传文件保存根路径。
     */
    String ROOT = "/upload/";

    /**
     * 处理多文件上传请求。
     *
     * @param content 上传数据。
     * @return 处理结果。
     */
    JSONArray uploads(String content);

    /**
     * 处理单个文件上传请求。
     *
     * @param fieldName   域名称[监听器KEY]。
     * @param fileName    文件名。
     * @param contentType 文件类型。
     * @param base64      Base64编码的文件数据。
     * @return 处理结果。
     */
    JSONObject upload(String fieldName, String fileName, String contentType, String base64);

    /**
     * 处理上传请求。
     *
     * @param readers 上传数据读取实例集。
     * @return 处理结果。
     * @throws IOException IO异常。
     */
    JSONArray uploads(List<UploadReader> readers) throws IOException;

    /**
     * 处理上传请求。
     *
     * @param reader 上传数据读取实例。
     * @return 处理结果。
     * @throws IOException IO异常。
     */
    JSONObject upload(UploadReader reader) throws IOException;

    /**
     * 删除上传的文件。
     *
     * @param key 上传key。
     * @param uri 文件URI地址。
     */
    void remove(String key, String uri);
}
