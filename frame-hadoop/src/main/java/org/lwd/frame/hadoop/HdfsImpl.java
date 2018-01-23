package org.lwd.frame.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.storage.Storage;
import org.lwd.frame.util.Io;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwd
 */
@Component("frame.hadoop.hdfs")
public class HdfsImpl implements Hdfs, Storage, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Value("${frame.hadoop.hdfs.url:}")
    private String url;
    private boolean disabled;
    private Configuration configuration;
    private ThreadLocal<FileSystem> fileSystem = new ThreadLocal<>();

    @Override
    public String getType() {
        return "hdfs";
    }

    @Override
    public List<String> list(String path, boolean recursive) {
        List<String> list = new ArrayList<>();
        if (isDisabled())
            return list;

        try {
            for (RemoteIterator<LocatedFileStatus> iterator = getFileSystem().listFiles(new Path(path), recursive); iterator.hasNext(); )
                list.add(iterator.next().getPath().toUri().getPath());
        } catch (IOException e) {
            logger.warn(e, "列出HDFS文件[{}]时发生异常！", path);
        }

        return list;
    }

    @Override
    public void mkdirs(String path) {
        if (isDisabled())
            return;

        try {
            getFileSystem().mkdirs(new Path(path));
        } catch (IOException e) {
            logger.warn(e, "创建HDFS路径[{}]时发生异常！", path);
        }
    }

    @Override
    public String getAbsolutePath(String path) {
        return path;
    }

    @Override
    public boolean exists(String absolutePath) {
        if (isDisabled())
            return false;

        try {
            return getFileSystem().exists(new Path(absolutePath));
        } catch (IOException e) {
            logger.warn(e, "验证HDFS文件[{}]是否存在时发生异常！", absolutePath);

            return false;
        }
    }

    @Override
    public long lastModified(String path) {
        if (isDisabled())
            return 0L;

        try {
            return getFileSystem().getFileStatus(new Path(path)).getModificationTime();
        } catch (IOException e) {
            logger.warn(e, "获取HDFS文件[{}]最后修改时间时发生异常！", path);

            return 0L;
        }
    }

    @Override
    public byte[] read(String path) {
        if (isDisabled())
            return new byte[0];

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        read(path, outputStream);
        try {
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "关闭输出流时发生异常！");
        }

        return outputStream.toByteArray();
    }

    @Override
    public void read(String path, OutputStream outputStream) {
        if (isDisabled())
            return;

        try {
            io.copy(getFileSystem().open(new Path(path)), outputStream);
        } catch (IOException e) {
            logger.warn(e, "读取HDFS文件[{}]时发生异常！", path);
        }
    }

    @Override
    public void write(String path, InputStream inputStream) throws IOException {
        write(inputStream, path);
    }

    @Override
    public void write(String path, byte[] bytes) throws IOException {
        write(bytes, path);
    }

    @Override
    public void write(InputStream inputStream, String path) {
        if (isDisabled())
            return;

        try {
            OutputStream outputStream = getFileSystem().create(new Path(path), true);
            io.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "写入HDFS文件[{}]时发生异常！", path);
        }
    }

    @Override
    public void write(byte[] data, String path) {
        if (isDisabled())
            return;

        try {
            OutputStream outputStream = getFileSystem().create(new Path(path), true);
            outputStream.write(data, 0, data.length);
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "写入HDFS文件[{}]时发生异常！", path);
        }
    }

    @Override
    public void delete(String path) {
        if (isDisabled())
            return;

        try {
            getFileSystem().delete(new Path(path), true);
        } catch (IOException e) {
            logger.warn(e, "删除HDFS文件[{}]时发生异常！", path);
        }
    }

    @Override
    public void upload(String file, String path) {
        if (isDisabled())
            return;

        try {
            getFileSystem().copyFromLocalFile(new Path(file), new Path(path));
        } catch (IOException e) {
            logger.warn(e, "上传文件[{}]到HDFS目录[{}]时发生异常！", file, path);
        }
    }

    @Override
    public void download(String file, String path) {
        if (isDisabled())
            return;

        try {
            getFileSystem().copyToLocalFile(new Path(file), new Path(path));
        } catch (IOException e) {
            logger.warn(e, "下载HDFS文件[{}]到目录[{}]时发生异常！", file, path);
        }
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        return getFileSystem().open(new Path(path));
    }

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        return getFileSystem().create(new Path(path), true);
    }

    private boolean isDisabled() {
        if (disabled)
            logger.warn(null, "HDFS环境[{}]初始化失败！", url);

        return disabled;
    }

    private FileSystem getFileSystem() throws IOException {
        FileSystem fileSystem = this.fileSystem.get();
        if (fileSystem == null) {
            this.fileSystem.set(fileSystem = FileSystem.get(configuration));
        }

        return fileSystem;
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        if (disabled = validator.isEmpty(url)) {
            if (logger.isDebugEnable())
                logger.debug("HDFS环境为空，不启用HDFS。");

            return;
        }

        configuration = new Configuration();
        configuration.set("fs.defaultFS", url);
        if (logger.isInfoEnable())
            logger.info("配置HDFS环境[{}]完成。", url);
    }

    @Override
    public void close() {
        FileSystem fileSystem = this.fileSystem.get();
        if (fileSystem == null)
            return;

        try {
            fileSystem.close();
            this.fileSystem.remove();
        } catch (IOException e) {
            logger.warn(e, "关闭HDFS文件系统时发生异常！");
        }
    }
}
