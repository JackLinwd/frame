package org.lwd.frame.ctrl.socket.context;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.context.HeaderAdapter;

/**
 * @author lwd
 */
public class HeaderAdapterImpl extends JsonSupport implements HeaderAdapter {
    private String ip;

    public HeaderAdapterImpl(JSONObject object, String ip) {
        super(object);
        this.ip = ip;
    }

    @Override
    public String getIp() {
        return ip;
    }
}
