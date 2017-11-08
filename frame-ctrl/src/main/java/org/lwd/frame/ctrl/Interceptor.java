package org.lwd.frame.ctrl;

/**
 * 控制层拦截器。
 *
 * @author lwd
 */
public interface Interceptor {
    /**
     * 获得拦截器的执行顺序。执行顺序小的优先执行。
     *
     * @return 执行顺序。
     */
    int getSort();

    /**
     * 执行拦截器调用。
     *
     * @param invocation 执行调用。
     * @return 执行结果。
     * @throws Exception 运行期异常。
     */
    Object execute(Invocation invocation) throws Exception;
}
