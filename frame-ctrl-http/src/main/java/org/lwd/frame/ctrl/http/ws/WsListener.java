package org.lwd.frame.ctrl.http.ws;

/**
 * WebSocket监听器。
 *
 * @author lwd
 */
public interface WsListener {
    /**
     * 处理连接打开事件。
     *
     * @param session 连接Session ID。
     */
    void open(String session);

    /**
     * 处理接收到新消息事件。
     *
     * @param session 连接Session ID。
     * @param message 消息。
     */
    void message(String session, String message);

    /**
     * 处理连接异常事件。
     *
     * @param session   连接Session ID。
     * @param throwable 异常。
     */
    void error(String session, Throwable throwable);

    /**
     * 处理连接关闭事件。
     *
     * @param session 连接Session ID。
     */
    void close(String session);
}
