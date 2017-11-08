package org.lwd.frame.ctrl.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于Map的请求参数适配器实现。
 *
 * @author lwd
 */
public class LocalRequestAdapter implements RequestAdapter {
    protected String uri;
    protected Map<String, String> map;

    public LocalRequestAdapter(String uri, Map<String, String> map) {
        this.uri = uri;
        this.map = map == null ? new HashMap<>() : map;
    }

    @Override
    public String get(String name) {
        return map.get(name);
    }

    @Override
    public String[] getAsArray(String name) {
        return null;
    }

    @Override
    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public String getFromInputStream() {
        return null;
    }

    @Override
    public String getServerName() {
        return "127.0.0.1";
    }

    @Override
    public int getServerPort() {
        return 80;
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getUrl() {
        return "http://127.0.0.1";
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return "POST";
    }
}
