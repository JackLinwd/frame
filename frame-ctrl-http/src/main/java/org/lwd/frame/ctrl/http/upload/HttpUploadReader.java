package org.lwd.frame.ctrl.http.upload;

import org.apache.commons.fileupload.FileItem;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.ctrl.upload.UploadReader;
import org.lwd.frame.storage.Storage;
import org.lwd.frame.util.Io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lwd
 */
public class HttpUploadReader implements UploadReader {
    private FileItem item;
    private String fieldName;
    private String fileName;
    private String contentType;
    private long size;
    private InputStream inputStream;
    private byte[] bytes;

    HttpUploadReader(FileItem item) throws IOException {
        this.item = item;
        fieldName = item.getFieldName();
        fileName = item.getName();
        contentType = item.getContentType();
        size = item.getSize();
        inputStream = item.getInputStream();
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, inputStream);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public byte[] getBytes() {
        if (bytes == null)
            bytes = BeanFactory.getBean(Io.class).read(inputStream);

        return bytes;
    }

    @Override
    public void delete() throws IOException {
        item.delete();
    }
}
