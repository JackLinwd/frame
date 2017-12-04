package org.lwd.frame.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片操作工具。
 *
 * @author lwd
 */
public interface Image {
    /**
     * 输出图片格式。
     */
    enum Format {
        /**
         * JPEG。
         */
        Jpeg,
        /**
         * GIF。
         */
        Gif,
        /**
         * PNG。
         */
        Png
    }

    /**
     * 读取图片。
     *
     * @param bytes 图片数据流。
     * @return 图片，如果读取失败则返回null。
     * @throws IOException 读取异常。
     */
    BufferedImage read(byte[] bytes) throws IOException;

    /**
     * 读取图片。
     *
     * @param inputStream 图片输入流。
     * @return 图片，如果读取失败则返回null。
     * @throws IOException 读取异常。
     */
    BufferedImage read(InputStream inputStream) throws IOException;

    /**
     * 获取缩略图。
     *
     * @param image     原图。
     * @param maxWidth  最大宽度。
     * @param maxHeight 最大高度。
     * @return 缩略图，如果失败则返回null。
     */
    BufferedImage thumbnail(BufferedImage image, int maxWidth, int maxHeight);

    /**
     * 裁切图片。
     *
     * @param image  原图。
     * @param x      裁切X位置。
     * @param y      裁切Y位置。
     * @param width  裁切宽度。
     * @param height 裁切高度。
     * @return 裁切后的图片，如果裁切失败则返回null。
     */
    BufferedImage subimage(BufferedImage image, int x, int y, int width, int height);

    /**
     * 输出图片。
     *
     * @param image  图片。
     * @param format 输出格式；默认Format.Png。
     * @return 输出流。
     * @throws IOException 写入异常。
     */
    byte[] write(BufferedImage image, Format format) throws IOException;

    /**
     * 将图片写入输出流。
     *
     * @param image        图片。
     * @param format       输出格式；默认Format.Png。
     * @param outputStream 输出流。
     * @throws IOException 写入异常。
     */
    void write(BufferedImage image, Format format, OutputStream outputStream) throws IOException;

    /**
     * 根据Content-Type获取图片格式。
     *
     * @param contentType 图片Content-Type。
     * @return 图片格式；默认返回Format.Png。
     */
    Format formatFromContentType(String contentType);
}