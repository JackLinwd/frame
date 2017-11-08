package org.lwd.frame.ctrl;

/**
 * 错误编码管理器。
 * 提供统一的错误编码获取功能，自动根据当前URI地址获取错误编码。
 *
 * @author lwd
 */
public interface FailureCode {
    /**
     * 获取错误编码。
     *
     * @param code 错误编码细项。
     * @return 错误编码；如果获取失败则返回-1。
     */
    int get(int code);

    /**
     * 获取错误编码。
     *
     * @param uri  URI地址。
     * @param code 错误编码细项。
     * @return 错误编码；如果获取失败则返回-1。
     */
    int get(String uri, int code);
}
