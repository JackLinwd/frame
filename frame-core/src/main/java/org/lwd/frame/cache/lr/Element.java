package org.lwd.frame.cache.lr;

import java.io.Serializable;

/**
 * 缓存元素。
 *
 * @author lwd
 */
public class Element implements Comparable<Element>, Serializable {
    private static final long serialVersionUID = 2825385973125777861L;

    protected String key;
    protected Object value;
    protected boolean resident;
    protected long lastVisitedTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isResident() {
        return resident;
    }

    public void setResident(boolean resident) {
        this.resident = resident;
    }

    public long getLastVisitedTime() {
        return lastVisitedTime;
    }

    public void setLastVisitedTime(long lastVisitedTime) {
        this.lastVisitedTime = lastVisitedTime;
    }

    @Override
    public int compareTo(Element o) {
        if (lastVisitedTime == o.lastVisitedTime)
            return 0;

        return lastVisitedTime > o.lastVisitedTime ? 1 : -1;
    }
}
