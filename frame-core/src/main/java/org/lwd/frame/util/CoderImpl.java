package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author lwd
 */
@Component("frame.util.coder")
public class CoderImpl implements Coder {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    @Inject
    private Context context;
    @Inject
    private Logger logger;
    private Charset charset = Charset.forName("ISO-8859-1");

    @Override
    public String hex(byte[] bytes) {
        if (bytes == null)
            return null;

        char[] chars = new char[bytes.length << 1];
        for (int i = 0; i < bytes.length; i++) {
            int n = i << 1;
            chars[n] = HEX[bytes[i] >> 4 & 0xf];
            chars[n + 1] = HEX[bytes[i] & 0xf];
        }

        return new String(chars);
    }

    @Override
    public String encodeUrl(String string, String charset) {
        if (string == null)
            return null;

        try {
            return URLEncoder.encode(string, context.getCharset(charset));
        } catch (UnsupportedEncodingException e) {
            logger.warn(e, "将字符串[{}]进行URL编码[{}]转换时发生异常！", string, charset);

            return string;
        }
    }

    @Override
    public String decodeUrl(String string, String charset) {
        if (string == null)
            return null;

        try {
            return URLDecoder.decode(string, context.getCharset(charset));
        } catch (UnsupportedEncodingException e) {
            logger.warn(e, "将字符串[{}]进行URL解码[{}]转换时发生异常！", string, charset);

            return string;
        }
    }

    @Override
    public String encodeBase64(byte[] bytes) {
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public byte[] decodeBase64(String string) {
        return Base64.getDecoder().decode(string);
    }

    @Override
    public String encodeMimeBase64(byte[] bytes) {
        return bytes == null ? null : Base64.getMimeEncoder().encodeToString(bytes);
    }

    @Override
    public byte[] decodeMimeBase64(String string) {
        return Base64.getMimeDecoder().decode(string);
    }

    @Override
    public String encodeUrlBase64(byte[] bytes) {
        return bytes == null ? null : Base64.getUrlEncoder().encodeToString(bytes);
    }

    @Override
    public byte[] decodeUrlBase64(String string) {
        return Base64.getUrlDecoder().decode(string);
    }

    @Override
    public String charset(String string) {
        return string == null ? null : new String(string.getBytes(charset));
    }

    @Override
    public String charset(String string, String charset) {
        return string == null ? null : new String(string.getBytes(this.charset), Charset.forName(charset));
    }
}
