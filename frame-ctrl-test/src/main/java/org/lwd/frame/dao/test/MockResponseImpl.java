package org.lwd.frame.dao.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
public class MockResponseImpl implements MockResponse {
    private String contentType;
    private OutputStream outputStream;
    private String url;

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public String getRedirectTo() {
        return url;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public OutputStream getOutputStream() {
        if (outputStream == null)
            outputStream = new ByteArrayOutputStream();

        return outputStream;
    }

    @Override
    public void send() throws IOException {
    }

    @Override
    public void redirectTo(String url) {
        this.url = url;
    }

    @Override
    public void sendError(int code) {
    }

    @Override
    public JSONObject asJson() {
        return JSON.parseObject(asString());
    }

    @Override
    public String asString() {
        return getOutputStream().toString();
    }
}
