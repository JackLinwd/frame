package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author lwd
 */
@Component("frame.util.compresser")
public class CompresserImpl implements Compresser {
    @Inject
    private Logger logger;

    @Override
    public byte[] zip(byte[] bytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            zip(bytes, outputStream);
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "使用ZIP压缩数据时发生异常！");
        }

        return outputStream.toByteArray();
    }

    @Override
    public void zip(byte[] bytes, OutputStream outputStream) {
        try {
            Deflater deflater = new Deflater();
            deflater.setInput(bytes);
            deflater.finish();
            byte[] buffer = new byte[bytes.length];
            for (int length; !deflater.finished() && (length = deflater.deflate(buffer)) > -1; )
                outputStream.write(buffer, 0, length);
            deflater.end();
        } catch (IOException e) {
            logger.warn(e, "使用ZIP压缩数据时发生异常！");
        }
    }

    @Override
    public byte[] unzip(byte[] bytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            unzip(bytes, outputStream);
            outputStream.close();
        } catch (IOException e) {
            logger.warn(e, "使用ZIP解压缩数据时发生异常！");
        }

        return outputStream.toByteArray();
    }

    @Override
    public void unzip(byte[] bytes, OutputStream outputStream) {
        try {
            Inflater inflater = new Inflater();
            inflater.setInput(bytes);
            byte[] buffer = new byte[1024];
            for (int length; !inflater.finished() && (length = inflater.inflate(buffer)) > -1; )
                outputStream.write(buffer, 0, length);
            inflater.end();
        } catch (Exception e) {
            logger.warn(e, "使用ZIP解压缩数据时发生异常！");
        }
    }
}
