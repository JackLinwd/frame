package org.lwd.frame.ctrl.http.upload;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author lwd
 */
public class JsonConfigImpl implements JsonConfig {
    private Map<Pattern, String> map;
    private int[] imageSize;
    private long lastModify;

    JsonConfigImpl() {
        map = new HashMap<>();
        imageSize = new int[2];
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void addPath(String contentType, String path) {
        map.put(Pattern.compile(contentType), path);
    }

    @Override
    public boolean isUploadEnable(String key, String contentType, String name) {
        for (Pattern pattern : map.keySet())
            if (pattern.matcher(contentType).find())
                return true;

        return false;
    }

    @Override
    public void setImageSize(int width, int height) {
        imageSize[0] = width;
        imageSize[1] = height;
    }

    @Override
    public long getLastModify() {
        return lastModify;
    }

    @Override
    public String getPath(String key, String contentType, String name) {
        for (Pattern pattern : map.keySet())
            if (pattern.matcher(contentType).find())
                return map.get(pattern);

        return null;
    }

    @Override
    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    @Override
    public int[] getImageSize(String key) {
        return imageSize;
    }

    @Override
    public String upload(String key, String name, String size, String uri) {
        return uri;
    }
}
