package org.lwd.frame.crypto;

/**
 * 标准加解密。
 *
 * @author lwd
 */
public interface Crypto {
    /**
     * 加密数据。
     *
     * @param key     密钥。
     * @param message 信息。
     * @return 密文；如果加密失败则返回null。
     */
    byte[] encrypt(byte[] key, byte[] message);

    /**
     * 解密数据。
     *
     * @param key     密钥。
     * @param message 密文。
     * @return 信息；如果解密失败则返回null。
     */
    byte[] decrypt(byte[] key, byte[] message);
}
