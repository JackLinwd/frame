package org.lwd.frame.ctrl.upload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.storage.Storage;
import org.lwd.frame.storage.Storages;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.DateTime;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Image;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Message;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Service(UploadService.PREFIX + "service")
public class UploadServiceImpl implements UploadService, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private Message message;
    @Inject
    private DateTime dateTime;
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private Image image;
    @Inject
    private Logger logger;
    @Inject
    private Storages storages;
    @Inject
    private JsonConfigs jsonConfigs;
    private Map<String, UploadListener> listeners;

    @Override
    public JSONArray uploads(String content) {
        if (validator.isEmpty(content))
            return new JSONArray();

        JSONArray uploads = json.toArray(content);
        if (validator.isEmpty(uploads))
            return new JSONArray();

        List<UploadReader> readers = new ArrayList<>();
        for (int i = 0, size = uploads.size(); i < size; i++)
            readers.add(new JsonUploadReader(uploads.getJSONObject(i)));

        try {
            return uploads(readers);
        } catch (IOException e) {
            logger.warn(e, "处理JSON方式上传文件时发生异常！");

            return new JSONArray();
        }
    }

    @Override
    public JSONArray uploads(List<UploadReader> readers) throws IOException {
        JSONArray array = new JSONArray();
        for (UploadReader reader : readers)
            array.add(upload(reader));

        return array;
    }

    @Override
    public JSONObject upload(String fieldName, String fileName, String contentType, String base64) {
        try {
            return upload(new JsonUploadReader(fieldName, fileName, contentType, base64));
        } catch (IOException e) {
            logger.warn(e, "处理文件[{}:{}:{}]上传时发生异常！", fieldName, fileName, contentType);

            return new JSONObject();
        }
    }

    private JSONObject upload(UploadReader reader) throws IOException {
        String fieldName = reader.getFieldName();
        UploadListener listener = getListener(fieldName);
        if (listener == null)
            return failure(reader, message.get(PREFIX + "listener.not-exists"));

        String contentType = listener.getContentType(reader.getFieldName(), reader.getContentType(), reader.getFileName());
        if (!listener.isUploadEnable(reader.getFieldName(), contentType, reader.getFileName())) {
            logger.warn(null, "无法处理文件上传请求[key={}&content-type={}&name={}]！",
                    reader.getFieldName(), contentType, reader.getFileName());

            return failure(reader, message.get(PREFIX + "disable", reader.getFieldName(), contentType, reader.getFileName()));
        }

        JSONObject object = listener.settle(reader);
        if (object == null)
            object = save(reader.getFieldName(), listener, reader, contentType);
        reader.delete();

        return object;
    }

    private UploadListener getListener(String key) {
        if (listeners.containsKey(key))
            return listeners.get(key);

        for (String k : listeners.keySet())
            if (validator.isMatchRegex(k, key))
                return listeners.get(k);

        UploadListener listener = jsonConfigs.get(key);
        if (listener == null)
            logger.warn(null, "无法获得上传监听器[{}]，文件上传失败！", key);

        return listener;
    }

    private JSONObject save(String key, UploadListener listener, UploadReader reader, String contentType) throws IOException {
        Storage storage = storages.get(listener.getStorage());
        if (storage == null) {
            logger.warn(null, "无法获得存储处理器[{}]，文件上传失败！", listener.getStorage());

            return failure(reader, message.get(PREFIX + "storage.not-exists", listener.getStorage()));
        }

        JSONObject object = new JSONObject();
        object.put("success", true);
        object.put("fieldName", reader.getFieldName());
        object.put("fileName", reader.getFileName());
        String path = getPath(listener, reader, contentType);
        object.put("path", listener.upload(key, reader.getFileName(), converter.toBitSize(reader.getSize()), path));
        reader.write(storage, path);
        String thumbnail = thumbnail(listener.getImageSize(key), storage, contentType, path);
        if (thumbnail != null)
            object.put("thumbnail", listener.upload(key, reader.getFileName(), converter.toBitSize(reader.getSize()), thumbnail));

        if (logger.isDebugEnable())
            logger.debug("保存上传[{}:{}]的文件[{}:{}:{}]。", reader.getFieldName(), reader.getFileName(), path,
                    thumbnail, converter.toBitSize(reader.getSize()));

        return object;
    }

    private String getPath(UploadListener listener, UploadReader reader, String contentType) {
        String name = reader.getFileName();
        StringBuilder path = new StringBuilder(ROOT).append(contentType).append('/')
                .append(listener.getPath(reader.getFieldName(), contentType, name)).append('/')
                .append(dateTime.toString(dateTime.today(), "yyyyMMdd")).append('/').append(generator.random(32));
        String suffix = listener.getSuffix(listener.getKey(), contentType, name);
        path.append(validator.isEmpty(suffix) ? name.substring(name.lastIndexOf('.')) : suffix);

        return path.toString().replaceAll("[/]+", "/");
    }

    private String thumbnail(int[] size, Storage storage, String contentType, String path) {
        if (size == null || size.length != 2 || (size[0] <= 0 && size[1] <= 0))
            return null;

        try {
            BufferedImage image = this.image.read(storage.getInputStream(path));
            if (image == null)
                return null;

            image = this.image.thumbnail(image, size[0], size[1]);
            if (image == null)
                return null;

            int indexOf = path.lastIndexOf('.');
            String thumbnail = path.substring(0, indexOf) + ".thumbnail" + path.substring(indexOf);
            this.image.write(image, this.image.formatFromContentType(contentType), storage.getOutputStream(thumbnail));

            return thumbnail;
        } catch (Exception e) {
            logger.warn(e, "生成压缩图片时发生异常！");

            return null;
        }
    }

    private JSONObject failure(UploadReader uploadReader, String message) {
        JSONObject object = new JSONObject();
        object.put("success", false);
        object.put("fieldName", uploadReader.getFieldName());
        object.put("fileName", uploadReader.getFileName());
        object.put("message", message);

        return object;
    }

    @Override
    public void remove(String key, String uri) {
        UploadListener listener = getListener(key);
        if (listener == null) {
            logger.warn(null, "无法获得上传监听key[{}]，删除失败！", key);

            return;
        }

        storages.get(listener.getStorage()).delete(uri);

        if (logger.isDebugEnable())
            logger.debug("删除上传的文件[{}:{}]。", listener.getStorage(), uri);
    }

    @Override
    public int getContextRefreshedSort() {
        return 19;
    }

    @Override
    public void onContextRefreshed() {
        if (listeners != null)
            return;

        listeners = new HashMap<>();
        BeanFactory.getBeans(UploadListener.class).forEach(listener -> listeners.put(listener.getKey(), listener));
    }
}
