package org.lwd.frame.ctrl.status;

import com.alibaba.fastjson.JSONObject;

/**
 * 服务状态。
 * 用于查看当前服务状态。
 *
 * @author lwd
 */
public interface Status {
    /**
     * 获取URI地址。
     *
     * @return URI地址。
     */
    String getUri();

    /**
     * 验证是否执行状态服务。
     *
     * @param uri 服务URI地址。
     * @return 如果是执行状态服务则返回true；否则返回false。
     */
    boolean isStatus(String uri);

    /**
     * 获取状态数据。
     *
     * @param counter 计数器。
     * @return 状态数据。
     */
    JSONObject execute(int counter);
}
