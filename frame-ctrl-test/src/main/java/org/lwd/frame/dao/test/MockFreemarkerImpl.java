package org.lwd.frame.dao.test;

import org.lwd.frame.ctrl.template.TemplateSupport;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
public class MockFreemarkerImpl extends TemplateSupport implements MockFreemarker {
    private String name;
    private Object data;

    @Override
    public String getType() {
        return "mock-freemarker";
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void process(String name, Object data, OutputStream output) throws IOException {
        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getData() {
        return data;
    }
}
