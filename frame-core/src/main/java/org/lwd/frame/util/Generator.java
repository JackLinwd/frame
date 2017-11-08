package org.lwd.frame.util;

/**
 * 生成器。
 *
 * @author lwd
 */
public interface Generator {
    /**
     * 生成一个随机字符串。
     *
     * @param length 字符串长度。
     * @return 随机字符串。
     */
    String random(int length);

    /**
     * 生成一个仅由数字字符组成的随机字符串。
     *
     * @param length 字符串长度。
     * @return 随机字符串。
     */
    String number(int length);

    /**
     * 生成一个仅由小写字母组成的随机字符串。
     *
     * @param length 字符串长度。
     * @return 随机字符串。
     */
    String chars(int length);

    /**
     * 生成一个随机整数。
     *
     * @param min 最小值。
     * @param max 最大值。
     * @return 随机整数。
     */
    int random(int min, int max);

    /**
     * 生成一个随机整数。
     *
     * @param min 最小值。
     * @param max 最大值。
     * @return 随机整数。
     */
    long random(long min, long max);

    /**
     * 生成一个UUID随机数。
     *
     * @return UUID随机数。
     */
    String uuid();
}
