package org.lwd.frame.storage;

/**
 * 文件存储处理器集。
 *
 * @author lwd
 */
public interface Storages {
    /**
     * 磁盘文件类型名。
     */
    String TYPE_DISK = "disk";

    /**
     * 获取默认文件处理器。
     *
     * @return 文件处理器；如果不存在则返回null。
     */
    Storage get();

    /**
     * 获取文件处理器。
     *
     * @param type 类型；为空则使用默认。
     * @return 文件处理器；如果不存在则返回null。
     */
    Storage get(String type);
}
