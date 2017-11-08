package org.lwd.frame.crypto;


import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwd
 */
public abstract class CipherSupport implements Crypto {
    @Inject
    protected Validator validator;
    @Inject
    protected Logger logger;
    private Map<String, SecretKey> secretKeys = new ConcurrentHashMap<>();

    @Override
    public byte[] encrypt(byte[] key, byte[] message) {
        return doFinal(key, message, Cipher.ENCRYPT_MODE);
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] message) {
        return doFinal(key, message, Cipher.DECRYPT_MODE);
    }

    protected byte[] doFinal(byte[] key, byte[] input, int mode) {
        if (validator.isEmpty(key) || validator.isEmpty(input))
            return null;

        try {
            SecretKey secretKey = getSecretKey(key);
            if (secretKey == null)
                return null;

            Cipher cipher = Cipher.getInstance(getAlgorithm());
            cipher.init(mode, secretKey);

            return cipher.doFinal(input);
        } catch (Exception e) {
            logger.warn(e, "使用密钥[{}]进行[{}]加/解密[{}]时发生异常！", new String(key), getAlgorithm(), new String(input));

            return null;
        }
    }

    protected SecretKey getSecretKey(byte[] key) {
        if (!validate(key))
            return null;

        StringBuilder sb = new StringBuilder();
        sb.append(getAlgorithm());
        for (byte by : key)
            sb.append(by);

        return secretKeys.computeIfAbsent(sb.toString(), k -> new SecretKeySpec(key, getAlgorithm()));
    }

    /**
     * 获取算法。
     *
     * @return 算法。
     */
    protected abstract String getAlgorithm();

    /**
     * 验证密钥是否合法。
     *
     * @param key 密钥。
     * @return 如果合法则返回true；否则返回false。
     */
    protected abstract boolean validate(byte[] key);
}
