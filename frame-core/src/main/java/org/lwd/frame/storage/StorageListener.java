package org.lwd.frame.storage;

/**
 * 存储监听器。
 *
 * @author lwd
 */
public interface StorageListener {
    /**
     * 获取存储类型。
     *
     * @return 存储类型。
     */
    String getStorageType();

    /**
     * 获取监听文件路径集。
     *
     * @return 监听文件路径集。
     */
    String[] getScanPathes();

    /**
     * 存储文件发生变化通知接口。
     *
     * @param path         变化文件路径。
     * @param absolutePath 绝对路径。
     */
    void onStorageChanged(String path, String absolutePath);
}
