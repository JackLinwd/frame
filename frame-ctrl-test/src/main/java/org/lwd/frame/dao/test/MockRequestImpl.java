package org.lwd.frame.dao.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
public class MockRequestImpl implements MockRequest {
    private String method;
    private String url;
    private String uri;
    private String serverName;
    private int serverPort;
    private String contextPath;
    private Map<String, String> parameters;
    private String content;

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public void addParameter(String name, String value) {
        getMap().put(name, value);
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String get(String name) {
        return getMap().get(name);
    }

    @Override
    public String[] getAsArray(String name) {
        return null;
    }

    @Override
    public Map<String, String> getMap() {
        if (parameters == null)
            parameters = new HashMap<>();

        return parameters;
    }

    @Override
    public String getFromInputStream() {
        return content;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return method;
    }
}
