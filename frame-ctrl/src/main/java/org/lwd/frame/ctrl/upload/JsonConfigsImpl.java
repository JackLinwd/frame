package org.lwd.frame.ctrl.upload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.scheduler.MinuteJob;
import org.lwd.frame.util.Context;
import org.lwd.frame.util.Io;
import org.lwd.frame.util.Json;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwd
 */
@Service(UploadService.PREFIX + "json-configs")
public class JsonConfigsImpl implements JsonConfigs, MinuteJob {
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Value("${" + UploadService.PREFIX + "json-configs:/WEB-INF/upload}")
    private String configs;
    private Map<String, JsonConfig> map;

    @Override
    public JsonConfig get(String key) {
        if (map == null)
            init();

        return map.get(key);
    }

    private synchronized void init() {
        if (map != null)
            return;

        map = new ConcurrentHashMap<>();
        executeMinuteJob();
    }

    @Override
    public void executeMinuteJob() {
        if (map == null)
            return;

        for (File file : new File(context.getAbsolutePath(configs)).listFiles()) {
            String key = file.getName().substring(0, file.getName().lastIndexOf('.'));
            JsonConfig config = map.get(key);
            if (config != null && config.getLastModify() == file.lastModified())
                continue;

            config = new JsonConfigImpl();
            JSONObject json = this.json.toObject(io.readAsString(file.getPath()));
            JSONObject path = json.getJSONObject("path");
            for (Object contentType : path.keySet())
                config.addPath(contentType.toString(), path.getString(contentType.toString()));
            JSONArray imageSize = json.getJSONArray("image-size");
            config.setImageSize(imageSize.getIntValue(0), imageSize.getIntValue(1));
            config.setLastModify(file.lastModified());
            map.put(key, config);
        }
    }
}
