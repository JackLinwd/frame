package org.lwd.frame.ctrl.socket.context;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.context.RequestAdapter;

/**
 * @author lwd
 */
public class RequestAdapterImpl extends JsonSupport implements RequestAdapter {
    private int port;
    private String uri;

    public RequestAdapterImpl(JSONObject object, int port, String uri) {
        super(object);
        this.port = port;
        this.uri = uri;
    }

    @Override
    public String[] getAsArray(String name) {
        return null;
    }

    @Override
    public String getFromInputStream() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return port;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return null;
    }
}
