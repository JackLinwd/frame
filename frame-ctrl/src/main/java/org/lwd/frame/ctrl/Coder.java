package org.lwd.frame.ctrl;

import java.util.Map;

/**
 * @author lwd
 */
public interface Coder {
    /**
     * 对请求参数进行解码。
     *
     * @param parameters 要解码的参数集。
     * @return 解码后的参数集。
     */
    Map<String, String> decode(Map<String, String> parameters);

    /**
     * 对输出结果进行编码。
     *
     * @param content 要输出的内容。
     * @return 编码后的输出内容。
     */
    byte[] encode(byte[] content);
}
