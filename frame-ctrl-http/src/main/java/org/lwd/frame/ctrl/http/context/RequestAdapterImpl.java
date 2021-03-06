package org.lwd.frame.ctrl.http.context;

import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.ctrl.context.RequestAdapter;
import org.lwd.frame.util.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
public class RequestAdapterImpl implements RequestAdapter {
    private HttpServletRequest request;
    private String url;
    private String uri;
    private Map<String, String> map;
    private String content;

    public RequestAdapterImpl(HttpServletRequest request, String uri) {
        this.request = request;
        this.uri = uri;
    }

    @Override
    public String get(String name) {
        return getMap().get(name);
    }

    @Override
    public String[] getAsArray(String name) {
        String[] array = request.getParameterValues(name);

        return array == null || array.length == 1 && array[0].indexOf(',') > -1 ? null : array;
    }

    @Override
    public Map<String, String> getMap() {
        if (map == null) {
            if (content == null)
                getFromInputStream();
            if (content.length() == 0)
                map = new HashMap<>();
            else {
                if (content.charAt(0) == '{')
                    fromJson();
                else
                    map = BeanFactory.getBean(Converter.class).toParameterMap(getFromInputStream());
            }
            request.getParameterMap().forEach((key, value) -> map.put(key, value[0]));
        }

        return map;
    }

    @Override
    public String getFromInputStream() {
        if (content != null)
            return content;

        String contentType = request.getHeader("Content-Type");
        if (!BeanFactory.getBean(Validator.class).isEmpty(contentType) && contentType.toLowerCase().contains("multipart/form-data"))
            return content = "";

        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    request.getInputStream(), "ISO8859-1"));

            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (buffer.length() == 0)
                content = "";
            else
                content = new String(buffer.toString().getBytes("ISO8859-1"), "UTF-8");
        } catch (IOException e) {
            BeanFactory.getBean(Logger.class).warn(e, "获取InputStream中的数据时发生异常！");
            return "";
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    BeanFactory.getBean(Logger.class).warn(e, "获取InputStream中的数据时发生异常！");
                    return "";
                }
            }
        }
        return content;
    }

    private void fromJson() {
        try {
            map = new HashMap<>();
            BeanFactory.getBean(Json.class).toObject(content).forEach((key, value) -> map.put(key, value.toString()));
        } catch (Throwable throwable) {
            BeanFactory.getBean(Logger.class).warn(throwable, "从JSON内容[{}]中获取参数集异常！", content);
        }
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public String getUrl() {
        if (url == null)
            url = request.getRequestURL().toString().replaceAll(uri, "");

        return url;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }
}
