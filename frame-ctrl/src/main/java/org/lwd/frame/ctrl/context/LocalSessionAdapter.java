package org.lwd.frame.ctrl.context;


import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.util.Generator;

/**
 * 基于字符串的Session适配器实现。
 *
 * @author lwd
 */
public class LocalSessionAdapter implements SessionAdapter {
    protected String id;

    public LocalSessionAdapter(String id) {
        this.id = id == null ? BeanFactory.getBean(Generator.class).random(32) : id;
    }

    @Override
    public String getId() {
        return id;
    }
}
