package org.lwd.frame.chrome;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.util.*;
import org.lwd.frame.util.Thread;
import org.lwd.frame.ws.WsClient;
import org.lwd.frame.ws.WsClientListener;
import org.lwd.frame.ws.WsClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Base64;

/**
 * @author lwd
 */
@Component("frame.chrome.client")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChromeClientImpl implements WsClientListener, ChromeClient {
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Thread thread;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private WsClients wsClients;
    @Value("${frame.chrome.max-wait:30}")
    private int maxWait;
    private WsClient wsClient;
    private String service;
    private String url;
    private int wait;
    private JSONObject message;
    private String result;

    @Override
    public ChromeClient set(String service, String url, int wait, JSONObject message) {
        this.service = service;
        this.url = url;
        this.wait = wait;
        this.message = message;

        return this;
    }

    @Override
    public boolean complete(byte[] message) {
        return message[message.length - 2] == '}' && message[message.length - 1] == '}';
    }

    @Override
    public byte[] call() throws Exception {
        JSONObject object = json.toObject(http.get("http://" + service + "/json/new", null, url));
        thread.sleep(wait, TimeUnit.Second);
        wsClient = wsClients.get();
        try {
            wsClient.connect(this, object.getString("webSocketDebuggerUrl"));
            for (int i = 0; i < maxWait; i++) {
                if (result != null)
                    break;

                thread.sleep(1, TimeUnit.Second);
            }
            if (result == null) {
                logger.warn(null, "请求[{}]等待[{}]秒未获得Chrome推送的数据！", message, maxWait);

                return null;
            }
        } catch (Throwable throwable) {
            logger.warn(throwable, "请求Chrome[{}]时发生异常！", object.toJSONString());
        } finally {
            wsClient.close();
            http.get("http://" + service + "/json/close/" + object.getString("id"), null, "");
        }

        if (logger.isDebugEnable())
            logger.debug("接收到Chrome推送的数据[{}]。", converter.toBitSize(result.length()));
        JSONObject obj = json.toObject(result);
        if (obj == null)
            return null;

        if (!obj.containsKey("result")) {
            logger.warn(null, "请求Chrome失败[{}]！", result);

            return null;
        }

        return Base64.getDecoder().decode(obj.getJSONObject("result").getString("data"));
    }

    @Override
    public void connect() {
        wsClient.send(message.toJSONString());
    }

    @Override
    public void receive(String message) {
        result = message;
    }

    @Override
    public void disconnect() {
    }
}
