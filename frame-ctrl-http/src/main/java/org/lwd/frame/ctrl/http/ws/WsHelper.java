package org.lwd.frame.ctrl.http.ws;

import javax.websocket.Session;

/**
 * WebSocket支持类。
 *
 * @author lwd
 */
public interface WsHelper {
    /**
     * WebSocket监听URI地址。
     */
    String URI = "/frame/ctrl-http/ws";

    /**
     * 处理连接打开事件。
     *
     * @param session 连接Session。
     */
    void open(Session session);

    /**
     * 处理接收到新消息事件。
     *
     * @param session 连接Session。
     * @param message 消息。
     */
    void message(Session session, String message);

    /**
     * 处理连接异常事件。
     *
     * @param session   连接Session。
     * @param throwable 异常。
     */
    void error(Session session, Throwable throwable);

    /**
     * 处理连接关闭事件。
     *
     * @param session 连接Session。
     */
    void close(Session session);

    /**
     * 发送消息到客户端。
     *
     * @param session 客户端Session ID。
     * @param message 消息。
     */
    void send(String session, String message);

    /**
     * 发送消息到所有客户端。
     *
     * @param message 消息。
     */
    void send(String message);

    /**
     * 关闭客户端连接。
     *
     * @param session 客户端Session ID。
     */
    void close(String session);

    /**
     * 关闭所有客户端连接。
     */
    void close();
}
