package org.lwd.frame.util;

/**
 * Created by linwd on 2017/5/31.
 */
public interface Compare {

    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param v1
     * @param v2
     * @return
     */
    int version(String v1, String v2);
}
