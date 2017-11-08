package org.lwd.frame.script;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
@Service("frame.script.arguments")
public class ArgumentsImpl implements Arguments {
    protected ThreadLocal<Map<String, Object>> map = new ThreadLocal<>();

    @Override
    public Object get(String name) {
        return all().get(name);
    }

    @Override
    public void set(String name, Object value) {
        all().put(name, value);
    }

    @Override
    public Map<String, Object> all() {
        Map<String, Object> map = this.map.get();
        if (map == null) {
            map = new HashMap<>();
            this.map.set(map);
        }

        return map;
    }
}
