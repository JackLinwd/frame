package org.lwd.frame.aio;

/**
 * 服务端监听器。
 *
 * @author lwd
 */
public interface AioServerListener extends AioListener {
    /**
     * 接受新连接。当监听端口完成新Socket连接时，通知此方法。
     *
     * @param sessionId 客户端Session ID。
     */
    void accept(String sessionId);
}
