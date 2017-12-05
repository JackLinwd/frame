package org.lwd.frame.test;

import java.util.List;

/**
 * 生成器切片。主要对随机数的生成进行控制。
 *
 * @author lwd
 */
public interface GeneratorAspect {
    /**
     * 设置生成随机字符串返回结果集。
     *
     * @param strings 生成随机字符串返回结果集。
     */
    void randomString(List<String> strings);

    /**
     * 设置生成随机整数返回结果集。
     *
     * @param ints 生成随机整数返回结果集。
     */
    void randomInt(List<Integer> ints);
}
