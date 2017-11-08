package org.lwd.frame.cache.lr;

import org.lwd.frame.cache.Handler;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Component("frame.cache.lr.handler")
public class HandlerImpl implements Handler {
    @Inject
    private Local local;
    @Inject
    private Remote remote;

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void put(String key, Object value, boolean resident) {
        Element element = new Element();
        element.setKey(key);
        element.setValue(value);
        element.setResident(resident);
        element.setLastVisitedTime(System.currentTimeMillis());
        local.put(element);
        remote.put(element);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        Element element = local.get(key);
        if (element == null)
            return null;

        element.setLastVisitedTime(System.currentTimeMillis());
        local.put(element);

        return (T) element.getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T remove(String key) {
        remote.remove(key);
        Element element = local.remove(key);

        return element == null ? null : (T) element.getValue();
    }
}
