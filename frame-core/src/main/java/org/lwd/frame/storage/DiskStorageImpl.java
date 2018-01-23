package org.lwd.frame.storage;

import org.lwd.frame.util.Context;
import org.lwd.frame.util.Io;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;

/**
 * @author lwd
 */
@Component("frame.storage.disk")
public class DiskStorageImpl implements Storage {
    @Inject
    private Context context;
    @Inject
    private Io io;

    @Override
    public String getType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public void mkdirs(String path) {
        new File(path).mkdirs();
    }

    @Override
    public String getAbsolutePath(String path) {
        return context.getAbsolutePath(path);
    }

    @Override
    public boolean exists(String absolutePath) {
        return new File(absolutePath).exists();
    }

    @Override
    public long lastModified(String path) {
        return new File(path).lastModified();
    }

    @Override
    public void read(String path, OutputStream outputStream) throws IOException {
        InputStream inputStream = getInputStream(path);
        io.copy(inputStream, outputStream);
        inputStream.close();
    }

    @Override
    public void write(String path, InputStream inputStream) throws IOException {
        OutputStream outputStream = getOutputStream(path);
        io.copy(inputStream, outputStream);
        outputStream.close();
    }

    @Override
    public void write(String path, byte[] bytes) throws IOException {
        OutputStream outputStream = getOutputStream(path);
        outputStream.write(bytes, 0, bytes.length);
        outputStream.close();
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        return new FileInputStream(getAbsolutePath(path, false));
    }

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        return new FileOutputStream(getAbsolutePath(path, true));
    }

    @Override
    public void delete(String path) {
        new File(getAbsolutePath(path, false)).delete();
    }

    private String getAbsolutePath(String path, boolean parent) {
        String absolutePath = context.getAbsolutePath(path);
        if (parent)
            new File(absolutePath.substring(0, absolutePath.lastIndexOf(File.separatorChar))).mkdirs();

        return absolutePath;
    }
}
