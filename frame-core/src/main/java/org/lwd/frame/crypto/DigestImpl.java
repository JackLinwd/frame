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
            return byteArrayToHex(MessageDigest.getInstance(algorithm).digest(input));
//            return Hex.encodeHexString(MessageDigest.getInstance(algorithm).digest(input));
        } catch (NoSuchAlgorithmException e) {
            logger.warn(e, "取消息摘要[{}]时发生异常！", algorithm);

            return null;
        }
    }

    //下面这个函数用于将字节数组换成成16进制的字符串
    private String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }
}