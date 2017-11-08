package org.lwd.frame.util;

/**
 * 数值转换器。
 *
 * @author lwd
 */
public interface Numeric {
    /**
     * 将对象转化为int数值。
     *
     * @param object 要转化的对象。
     * @return int数值；如果转化失败则返回0。
     */
    int toInt(Object object);

    /**
     * 将对象转化为int数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return int数值；如果转化失败则返回默认值。
     */
    int toInt(Object object, int defaultValue);

    /**
     * 将对象转化为long数值。
     *
     * @param object 要转化的对象。
     * @return long数值；如果转化失败则返回0。
     */
    long toLong(Object object);

    /**
     * 将对象转化为long数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return long数值；如果转化失败则返回默认值。
     */
    long toLong(Object object, long defaultValue);

    /**
     * 将对象转化为float数值。
     *
     * @param object 要转化的对象。
     * @return float数值；如果转化失败则返回0.0。
     */
    float toFloat(Object object);

    /**
     * 将对象转化为float数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return float数值；如果转化失败则返回默认值。
     */
    float toFloat(Object object, float defaultValue);

    /**
     * 将对象转化为double数值。
     *
     * @param object 要转化的对象。
     * @return double数值；如果转化失败则返回0.0。
     */
    double toDouble(Object object);

    /**
     * 将对象转化为double数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return double数值；如果转化失败则返回默认值。
     */
    double toDouble(Object object, double defaultValue);

    /**
     * 将字符串转化为int数组，字符串中数值以逗号分割。
     *
     * @param string 数值组字符串。
     * @return int数组；如果转化失败则返回空数组。
     */
    int[] toInts(String string);

    /**
     * 将数值字符串数组转化为数值数组。
     *
     * @param array 数值字符串数组。
     * @return int数组；如果元素转化失败则默认为0。
     */
    int[] toInts(String[] array);

    /**
     * 按指定格式将数值格式化为字符串。
     *
     * @param number 要进行格式化的数值。
     * @param format 目标格式。
     * @return 格式化后的数值字符串。
     */
    String toString(Number number, String format);
}
