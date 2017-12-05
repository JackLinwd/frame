package org.lwd.frame.dao.test;

/**
 * Mock环境支持接口。
 *
 * @author lwd
 */
public interface MockHelper {
    /**
     * 获取Mock请求头实例。
     *
     * @return Mock请求头实例。
     */
    MockHeader getHeader();

    /**
     * 获取Mock请求Session环境。
     *
     * @return Mock请求Session环境。
     */
    MockSession getSession();

    /**
     * 获取Mock请求实例。
     *
     * @return Mock请求实例。
     */
    MockRequest getRequest();

    /**
     * 获取Mock输出实例。
     *
     * @return Mock输出实例。
     */
    MockResponse getResponse();

    /**
     * 获取Mock模板。
     *
     * @return Mock模板。
     */
    MockFreemarker getFreemarker();

    /**
     * 重置Mock环境。
     */
    void reset();

    /**
     * 以Mock方式执行请求。
     *
     * @param uri 请求URI地址。
     */
    void mock(String uri);

    /**
     * 以Mock方式执行请求。
     *
     * @param uri        请求URI地址。
     * @param freemarker 是否Mock Freemarker模板。
     */
    void mock(String uri, boolean freemarker);
}
