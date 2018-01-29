package org.lwd.frame.util;

/**
 * 编码/解码器。
 *
 * @author lwd
 */
public interface Coder {
    /**
     * 将字符串进行URL编码转换。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则使用默认编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    String encodeUrl(String string, String charset);

    /**
     * 将字符串进行URL解码。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则使用默认编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    String decodeUrl(String string, String charset);

    /**
     * 将数据进行BASE64编码。
     *
     * @param bytes 要编码的数据。
     * @return 编码后的字符串。
     */
    String encodeBase64(byte[] bytes);

    /**
     * 将数据进行BASE64解码。
     *
     * @param string 要解码的数据。
     * @return 解码后的数据。
     */
    byte[] decodeBase64(String string);

    /**
     * 将数据进行Mime BASE64编码。
     *
     * @param bytes 要编码的数据。
     * @return 编码后的字符串。
     */
    String encodeMimeBase64(byte[] bytes);

    /**
     * 将数据进行Mime BASE64解码。
     *
     * @param string 要解码的数据。
     * @return 解码后的数据。
     */
    byte[] decodeMimeBase64(String string);

    /**
     * 将数据进行URL BASE64编码。
     *
     * @param bytes 要编码的数据。
     * @return 编码后的字符串。
     */
    String encodeUrlBase64(byte[] bytes);

    /**
     * 将数据进行URL BASE64解码。
     *
     * @param string 要解码的数据。
     * @return 解码后的数据。
     */
    byte[] decodeUrlBase64(String string);

    /**
     * 转化字符串为当前字符集编码的字符串。
     *
     * @param string 字符串。
     * @return 字符串。
     */
    String charset(String string);

    /**
     * 转化字符串为指定字符集编码的字符串。
     *
     * @param string  字符串。
     * @param charset 字符集。
     * @return 字符串。
     */
    String charset(String string, String charset);
}
