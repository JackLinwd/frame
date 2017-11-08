package org.lwd.frame.script;

import java.util.List;

/**
 * @author lwd
 */
public interface Engine {
    /**
     * 重新加载脚本文件。
     */
    void refresh();

    /**
     * 验证是否存在请求的方法名。
     *
     * @return 如果存在则返回true；否则返回false。
     */
    boolean existsMethod();

    /**
     * 执行脚本方法。
     *
     * @param name 方法名。
     * @return 执行结果。
     */
    Object execute(String name);

    /**
     * 获取脚本文件名集。
     *
     * @return 脚本文件名集。
     */
    List<String> names();

    /**
     * 读取脚本文件。
     *
     * @param name 脚本文件名。
     * @return 文件内容。
     */
    String read(String name);
}
