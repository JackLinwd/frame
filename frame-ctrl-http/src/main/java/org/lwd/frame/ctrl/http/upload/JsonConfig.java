package org.lwd.frame.ctrl.http.upload;

/**
 * @author lwd
 */
public interface JsonConfig extends UploadListener {
    /**
     * 添加保存路径设置。
     *
     * @param contentType 文件类型。
     * @param path        保存路径。
     */
    void addPath(String contentType, String path);

    /**
     * 设置图片文件宽高。
     *
     * @param width  宽。
     * @param height 高。
     */
    void setImageSize(int width, int height);

    /**
     * 获取最后修改时间。
     *
     * @return 最后修改时间。
     */
    long getLastModify();

    /**
     * 设置最后修改时间。
     *
     * @param lastModify 最后修改时间。
     */
    void setLastModify(long lastModify);
}
