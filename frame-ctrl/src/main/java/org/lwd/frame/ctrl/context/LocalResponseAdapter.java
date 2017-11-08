package org.lwd.frame.ctrl.context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 基于内存的输出适配器实现。
 *
 * @author lwd
 */
public class LocalResponseAdapter implements ResponseAdapter {
    protected OutputStream outputStream;

    public LocalResponseAdapter() {
        outputStream = new ByteArrayOutputStream();
    }

    @Override
    public void setContentType(String contentType) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public void send() throws IOException {
    }

    @Override
    public void redirectTo(String url) {
    }

    @Override
    public void sendError(int code) {
    }
}
