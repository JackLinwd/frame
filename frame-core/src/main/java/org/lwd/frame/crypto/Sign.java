package org.lwd.frame.crypto;

import java.util.Map;

/**
 * 签名。
 *
 * @author lwd
 */
public interface Sign {
    /**
     * 添加签名到Map集合中。
     *
     * @param map  要添加签名的Map集合。
     * @param name 密钥名。
     */
    void put(Map<String, String> map, String name);

    /**
     * 验证签名。
     *
     * @param map  签名数据集。
     * @param name 密钥名。
     * @return 如果验证通过则返回true；否则返回false。
     */
    boolean verify(Map<String, String> map, String name);
}
