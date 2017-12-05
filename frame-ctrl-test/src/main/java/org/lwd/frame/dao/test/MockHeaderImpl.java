package org.lwd.frame.dao.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
public class MockHeaderImpl implements MockHeader {
    private String ip;
    private Map<String, String> map;

    @Override
    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public void addHeader(String name, String value) {
        getMap().put(name, value);
    }

    @Override
    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String get(String name) {
        return getMap().get(name);
    }

    @Override
    public String getIp() {
        return ip == null ? "127.0.0.1" : ip;
    }

    @Override
    public Map<String, String> getMap() {
        if (map == null)
            map = new HashMap<>();

        return map;
    }
}
