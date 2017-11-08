package org.lwd.frame.bean;

import java.lang.reflect.Field;

/**
 * @author lwd
 */
public class Injecter {
    protected Object bean;
    protected Field field;
    protected boolean collection;

    public Injecter(Object bean, Field field, boolean collection) {
        this.bean = bean;
        this.field = field;
        this.collection = collection;
    }

    public Object getBean() {
        return bean;
    }

    public Field getField() {
        return field;
    }

    public boolean isCollection() {
        return collection;
    }
}
