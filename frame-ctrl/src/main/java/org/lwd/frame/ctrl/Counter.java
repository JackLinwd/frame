package org.lwd.frame.ctrl;

/**
 * @author lwd
 */
public interface Counter {
    /**
     * 增加访问计数。
     *
     * @param uri 请求URI地址。
     * @param ip  请求方IP地址。
     * @return 如果允许访问则返回true；否则返回false。
     */
    boolean increase(String uri, String ip);

    /**
     * 获取当前请求总数。
     *
     * @return 当前请求总数。
     */
    int get();

    /**
     * 减少访问计数。
     *
     * @param uri 请求URI地址。
     * @param ip  请求方IP地址。
     */
    void decrease(String uri, String ip);

    /**
     * 获取最大并发请求总数设置。
     *
     * @return 最大并发请求总数设置。
     */
    int max();
}
