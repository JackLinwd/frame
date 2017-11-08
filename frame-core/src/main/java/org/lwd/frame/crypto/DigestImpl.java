package org.lwd.frame.crypto;

import org.apache.commons.codec.binary.Hex;
import org.lwd.frame.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lwd
 */
@Component("frame.crypto.digest")
public class DigestImpl implements Digest {
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA1";

    @Inject
    private Logger logger;

    @Override
    public String md5(String text) {
        return text == null ? null : digest(MD5, text.getBytes());
    }

    @Override
    public String md5(byte[] text) {
        return digest(MD5, text);
    }

    @Override
    public String sha1(String text) {
        return text == null ? null : digest(SHA1, text.getBytes());
    }

    @Override
    public String sha1(byte[] text) {
        return digest(SHA1, text);
    }

    private String digest(String algorithm, byte[] input) {
        if (input == null)
            return null;

        try {
            return Hex.encodeHexString(MessageDigest.getInstance(algorithm).digest(input));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(e, "取消息摘要[{}]时发生异常！", algorithm);

            return null;
        }
    }
}
