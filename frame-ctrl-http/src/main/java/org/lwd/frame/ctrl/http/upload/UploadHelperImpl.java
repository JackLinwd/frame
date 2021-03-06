package org.lwd.frame.ctrl.http.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.lwd.frame.atomic.Closables;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.ctrl.http.IgnoreUri;
import org.lwd.frame.ctrl.http.ServiceHelper;
import org.lwd.frame.ctrl.upload.UploadReader;
import org.lwd.frame.ctrl.upload.UploadService;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Service(UploadHelper.PREFIX + "helper")
public class UploadHelperImpl implements UploadHelper, IgnoreUri {
    @Inject
    private Converter converter;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private Closables closables;
    @Inject
    private UploadService uploadService;
    @Inject
    private ServiceHelper serviceHelper;
    @Value("${" + UploadHelper.PREFIX + "max-size:1m}")
    private String maxSize;
    private ServletFileUpload upload;
    private Map<String, Uploader> uploaders;

    @Override
    public void upload(HttpServletRequest request, HttpServletResponse response, String uploader) {
        try {
            serviceHelper.setCors(request, response);
            OutputStream outputStream = serviceHelper.setContext(request, response, UPLOAD);
            List<UploadReader> readers = new ArrayList<>();
            for (FileItem item : getUpload(request).parseRequest(request))
                if (!item.isFormField())
                    readers.add(new HttpUploadReader(item));
            if (readers.isEmpty())
                return;

            outputStream.write(uploaders.get(uploader).upload(readers));
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
                if (uploaders == null) {
                    uploaders = new HashMap<>();
                    BeanFactory.getBeans(Uploader.class).forEach(uploader -> uploaders.put(uploader.getName(), uploader));
                }
            }

        }

        return upload;
    }

    @Override
    public String[] getIgnoreUris() {
        return new String[]{UPLOAD, UPLOAD_PATH};
    }
}
