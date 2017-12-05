package org.lwd.frame.ctrl.http.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.lwd.frame.atomic.Closables;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.ctrl.http.IgnoreUri;
import org.lwd.frame.ctrl.http.ServiceHelper;
import org.lwd.frame.storage.Storage;
import org.lwd.frame.storage.Storages;
import org.lwd.frame.util.Context;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.DateTime;
import org.lwd.frame.util.Generator;
import org.lwd.frame.util.Image;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Service("frame.ctrl.http.upload-helper")
public class UploadHelperImpl implements UploadHelper, IgnoreUri, ContextRefreshedListener {
    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private DateTime dateTime;
    @Inject
    private Image image;
    @Inject
    private Logger logger;
    @Inject
    private Storages storages;
    @Inject
    private Closables closables;
    @Inject
    private ServiceHelper serviceHelper;
    @Inject
    private JsonConfigs jsonConfigs;
    @Value("${frame.ctrl.http.upload.max-size:1m}")
    private String maxSize;
    private ServletFileUpload upload;
    private Map<String, UploadListener> listeners;

    @Override
    public void upload(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<FileItem> items = new ArrayList<>();
            getUpload(request).parseRequest(request).forEach(item -> {
                if (item.isFormField())
                    return;

                items.add(item);
            });

            OutputStream outputStream = serviceHelper.setContext(request, response, URI);
            StringBuilder sb = new StringBuilder();
            for (FileItem item : items) {
                String result = save(item);
                if (!validator.isEmpty(result))
                    sb.append(';').append(result);
            }

            if (sb.length() > 0)
                outputStream.write(sb.substring(1).getBytes(context.getCharset(null)));
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            logger.warn(e, "处理文件上传时发生异常！");
        } finally {
            closables.close();
        }
    }

    private ServletFileUpload getUpload(HttpServletRequest request) {
        if (upload == null) {
            synchronized (this) {
                if (upload == null) {
                    DiskFileItemFactory factory = new DiskFileItemFactory();
                    factory.setRepository((File) request.getServletContext().getAttribute("javax.servlet.context.tempdir"));
                    upload = new ServletFileUpload(factory);
                    upload.setSizeMax(converter.toBitSize(maxSize));
                }
            }
        }

        return upload;
    }

    private String save(FileItem item) throws IOException {
        String key = item.getFieldName();
        UploadListener listener = getListener(key);
        if (listener == null)
            return null;

        String contentType = listener.getContentType(key, item.getContentType(), item.getName());
        if (!listener.isUploadEnable(key, contentType, item.getName())) {
            logger.warn(null, "无法处理文件上传请求[key={}&content-type={}&name={}]！",
                    key, contentType, item.getName());

            return null;
        }

        Storage storage = storages.get(listener.getStorage());
        if (storage == null) {
            logger.warn(null, "无法获得存储处理器[{}]，文件上传失败！", listener.getStorage());

            return null;
        }
        String path = getPath(listener, item, contentType);
        storage.write(path, item.getInputStream());
        String thumbnail = thumbnail(listener.getImageSize(key), storage, contentType, path);
        String result = listener.upload(key, item.getName(), converter.toBitSize(item.getSize()),
                thumbnail == null ? path : (path + "," + thumbnail));
        item.delete();

        if (logger.isDebugEnable())
            logger.debug("保存上传[{}:{}]的文件[{}:{}:{}]。", item.getFieldName(), item.getName(), path,
                    thumbnail, converter.toBitSize(item.getSize()));

        return result;
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

    private String getPath(UploadListener listener, FileItem item, String contentType) {
        String name = item.getName();
        StringBuilder path = new StringBuilder(ROOT).append(contentType).append('/')
                .append(listener.getPath(item.getFieldName(), contentType, name)).append('/')
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
    public String[] getIgnoreUris() {
        return new String[]{URI};
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