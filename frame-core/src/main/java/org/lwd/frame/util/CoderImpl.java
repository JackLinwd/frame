package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * @author lwd
 */
@Component("frame.util.coder")
public class CoderImpl implements Coder {
    @Inject
    private Context context;
    @Inject
    private Logger logger;

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
        return bytes == null ? null : Base64.getUrlEncoder().encodeToString(bytes);
    }

    @Override
    public byte[] decodeBase64(String string) {
        return Base64.getUrlDecoder().decode(string);
    }
}
