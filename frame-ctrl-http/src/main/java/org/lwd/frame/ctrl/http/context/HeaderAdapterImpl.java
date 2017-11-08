package org.lwd.frame.ctrl.http.context;

import org.lwd.frame.ctrl.context.HeaderAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
public class HeaderAdapterImpl implements HeaderAdapter {
    protected HttpServletRequest request;

    public HeaderAdapterImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String get(String name) {
        return request.getHeader(name);
    }

    @Override
    public String getIp() {
        return request.getRemoteAddr();
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            map.put(name, request.getHeader(name));
        }

        return map;
    }
}
