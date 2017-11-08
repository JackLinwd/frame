package org.lwd.frame.ctrl.context;

import org.lwd.frame.cache.Cache;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Controller("frame.ctrl.context.session")
public class SessionImpl implements Session, SessionAware {
    private static final String CACHE = "frame.ctrl.context.session:";

    @Inject
    private Cache cache;
    private ThreadLocal<SessionAdapter> adapter = new ThreadLocal<>();

    @Override
    public void set(String key, Object value) {
        set(getId(), key, value);
    }

    @Override
    public void set(String id, String key, Object value) {
        cache.put(getKey(id, key), value, false);
    }

    @Override
    public <T> T get(String key) {
        return get(getId(), key);
    }

    @Override
    public <T> T get(String id, String key) {
        return cache.get(getKey(id, key));
    }

    @Override
    public void remove(String key) {
        remove(getId(), key);
    }

    @Override
    public void remove(String id, String key) {
        cache.remove(getKey(id, key));
    }

    private String getKey(String id, String key) {
        return CACHE + id + key;
    }

    @Override
    public String getId() {
        return adapter.get() == null ? null : adapter.get().getId();
    }

    @Override
    public void set(SessionAdapter adapter) {
        this.adapter.set(adapter);
    }
}
