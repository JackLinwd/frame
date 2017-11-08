package org.lwd.frame.carousel;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 本地服务。
 *
 * @author lwd
 */
public interface LocalService extends Callable<String> {
    /**
     * 创建本地服务。
     *
     * @param uri       URI地址。
     * @param ip        IP地址。
     * @param sessionId Session ID。
     * @param header    头信息集。
     * @param parameter 参数集。
     * @return 当前实例。
     */
    LocalService build(String uri, String ip, String sessionId, Map<String, String> header, Map<String, String> parameter);
}
