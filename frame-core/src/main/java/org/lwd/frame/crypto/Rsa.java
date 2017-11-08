package org.lwd.frame.crypto;

import java.io.OutputStream;

/**
 * RSA非对称加解密。
 *
 * @author lwd
 */
public interface Rsa {
    /**
     * 密钥类型。
     */
    enum KeyType {
        /**
         * 公钥。
         */
        Public,
        /**
         * 私钥。
         */
        Private
    }

    /**
     * 生成密钥对。
     *
     * @param publicKeyDer  DER格式的公钥。
     * @param publicKeyX509 X509格式的公钥。
     * @param privateKey    私钥。
     */
    void generate(OutputStream publicKeyDer, OutputStream publicKeyX509, OutputStream privateKey);

    /**
     * 加密。
     *
     * @param type    密钥类型。
     * @param key     密钥。
     * @param message 要加密的信息。
     * @return 加密后的密文；如果加密失败则返回null。
     */
    byte[] encrypt(KeyType type, byte[] key, byte[] message);

    /**
     * 解密。
     *
     * @param type    密钥类型。
     * @param key     密钥。
     * @param message 要解密的密文。
     * @return 解密后的明文；如果解密失败则返回null。
     */
    byte[] decrypt(KeyType type, byte[] key, byte[] message);
}
