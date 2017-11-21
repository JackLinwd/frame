package org.lwd.frame.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 二维码读写工具。
 *
 * @author lwd
 */
public interface QrCode {
    /**
     * @param content 二维码内容
     * @return
     */
    String create(String content);
    /**
     * 生成二维码图片。
     *
     * @param content 二维码内容。
     * @param size    二维码图片大小（长&宽）。
     * @param logo    LOGO文件地址，为空则不添加LOGO。
     * @param path    二维码图片输出路径。
     */
    void create(String content, int size, String logo, String path);

    /**
     * 生成二维码图片。
     *
     * @param content      二维码内容。
     * @param size         二维码图片大小（长&宽）。
     * @param logo         LOGO文件地址，为空则不添加LOGO。
     * @param outputStream 二维码图片输出流。
     */
    void create(String content, int size, String logo, OutputStream outputStream);

    /**
     * 生成二维码图片。
     *
     * @param content      二维码内容。
     * @param size         二维码图片大小（长&宽）。
     * @param logo         LOGO输入流，为空则不添加LOGO。
     * @param outputStream 二维码图片输出流。
     */
    void create(String content, int size, InputStream logo, OutputStream outputStream);

    /**
     * 生成二维码图片，并返回图片的Base64数据。
     *
     * @param content 二维码内容。
     * @param size    二维码图片大小（长&宽）。
     * @param logo    LOGO文件地址，为空则不添加LOGO。
     * @return 图片的Base64数据；如果生成失败将返回null。
     */
    String create(String content, int size, String logo);

    /**
     * 生成二维码图片，并返回图片的Base64数据。
     *
     * @param content 二维码内容。
     * @param size    二维码图片大小（长&宽）。
     * @param logo    LOGO输入流，为空则不添加LOGO。
     * @return 图片的Base64数据；如果生成失败将返回null。
     */
    String create(String content, int size, InputStream logo);

    /**
     * 读取二维码内容。
     *
     * @param path 二维码图片地址。
     * @return 二维码内容；读取失败则返回null。
     */
    String read(String path);

    /**
     * 读取二维码内容。
     *
     * @param inputStream 二维码图片输入流。
     * @return 二维码内容；读取失败则返回null。
     */
    String read(InputStream inputStream);
}
