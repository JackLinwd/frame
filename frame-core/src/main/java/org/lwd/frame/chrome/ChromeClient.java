package org.lwd.frame.chrome;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.Callable;

/**
 * Chrome连接客户端。
 *
 * @author lwd
 */
public interface ChromeClient extends Callable<byte[]> {
    /**
     * 设置请求参数。
     *
     * @param service 服务地址。
     * @param url     请求URL地址。
     * @param wait    等待时长，单位：秒。
     * @param message 发送指令。
     * @return 当前ChromeClient实例。
     */
    ChromeClient set(String service, String url, int wait, JSONObject message);
}
