package org.lwd.frame.ctrl.upload;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

/**
 * 上传文件处理监听器。
 *
 * @author lwd
 */
public interface UploadListener {
    /**
     * 获取监听器key；监听器key必须与上传字段名一致。
     *
     * @return 监听器key，支持正则表达式。
     */
    String getKey();

    /**
     * 获取文件content-type属性。
     *
     * @param key         上传文件key。
     * @param contentType 文件类型。
     * @param name        文件名。
     * @return 文件content-type属性。
     */
    default String getContentType(String key, String contentType, String name) {
        return contentType;
    }

    /**
     * 验证是否允许上传。
     *
     * @param key         上传文件key。
     * @param contentType 文件类型。
     * @param name        文件名。
     * @return 如果允许则返回true；否则返回false。
     */
    boolean isUploadEnable(String key, String contentType, String name);

    /**
     * 处理数据。
     *
     * @param uploadReader 上传数据读取器。
     * @return 处理后的数据。如果返回为null则继续执行存储操作，否则不存储。
     * @throws IOException IO异常。
     */
    default JSONObject settle(UploadReader uploadReader) throws IOException {
        return null;
    }

    /**
     * 获取存储处理器。
     *
     * @return 存储处理器。
     */
    default String getStorage() {
        return null;
    }

    /**
     * 获取文件保存路径。
     *
     * @param key         上传文件key。
     * @param contentType 文件类型。
     * @param name        文件名。
     * @return 文件保存路径。
     */
    default String getPath(String key, String contentType, String name) {
        return "";
    }

    /**
     * 获取文件保存后缀。
     *
     * @param key         上传文件key。
     * @param contentType 文件类型。
     * @param name        文件名。
     * @return 文件后缀；null表示使用默认后缀。
     */
    default String getSuffix(String key, String contentType, String name) {
        return null;
    }

    /**
     * 获取图片大小。
     * 当上传文件为图片时，并且返回的图片大小（长、高）大于0时，自动将图片修改为长宽不超过设置值的图片，并进行压缩。
     *
     * @param key 上传文件key。
     * @return 图片大小[长, 高]，如果返回空或0集则表示不需要调整图片。
     */
    default int[] getImageSize(String key) {
        return null;
    }

    /**
     * 上传完成。
     *
     * @param object 处理结果。
     */
    default void complete(JSONObject object) {
    }
}
