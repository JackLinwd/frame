package org.lwd.frame.crypto;

/**
 * 消息摘要。
 *
 * @author lwd
 */
public interface Digest {
    /**
     * 提取MD5消息摘要。
     *
     * @param text 源字符串。
     * @return MD5消息摘要；如果提取失败将返回null。
     */
    String md5(String text);

    /**
     * 提取MD5消息摘要。
     *
     * @param text 源数据。
     * @return MD5消息摘要；如果提取失败将返回null。
     */
    String md5(byte[] text);

    /**
     * 提取SHA1消息摘要。
     *
     * @param text 源字符串。
     * @return SHA1消息摘要；如果提取失败将返回null。
     */
    String sha1(String text);

    /**
     * 提取SHA1消息摘要。
     *
     * @param text 源数据。
     * @return SHA1消息摘要；如果提取失败将返回null。
     */
    String sha1(byte[] text);
}
