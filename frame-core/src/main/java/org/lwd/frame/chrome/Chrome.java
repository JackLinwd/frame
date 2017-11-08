package org.lwd.frame.chrome;

/**
 * Google Chrome DevTools协议工具。
 * https://chromedevtools.github.io/devtools-protocol/
 *
 * @author lwd
 */
public interface Chrome {
    /**
     * 输出PDF文档。
     *
     * @param url    URL地址。
     * @param wait   等待时间，单位：秒。
     * @param width  页面宽度，单位：像素。
     * @param height 页面高度，单位：像素。
     * @param range  输出的页面。
     * @return PDF数据。
     */
    byte[] pdf(String url, int wait, int width, int height, String range);

    /**
     * 输出PNG图片。
     *
     * @param url    URL地址。
     * @param wait   等待时间，单位：秒。
     * @param x      X位置，单位：像素。
     * @param y      Y位置，单位：像素。
     * @param width  页面宽度，单位：像素。
     * @param height 页面高度，单位：像素。
     * @return PNG数据。
     */
    byte[] png(String url, int wait, int x, int y, int width, int height);

    /**
     * 输出JPEG图片。
     *
     * @param url    URL地址。
     * @param wait   等待时间，单位：秒。
     * @param x      X位置，单位：像素。
     * @param y      Y位置，单位：像素。
     * @param width  页面宽度，单位：像素。
     * @param height 页面高度，单位：像素。
     * @return PNG数据。
     */
    byte[] jpeg(String url, int wait, int x, int y, int width, int height);
}
