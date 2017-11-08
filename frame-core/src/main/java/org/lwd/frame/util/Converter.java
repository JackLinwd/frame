package org.lwd.frame.util;

import java.util.Map;
import java.util.Set;

/**
 * 转换器。提供常用转换操作。
 *
 * @author lwd
 */
public interface Converter {
    /**
     * 将对象转化为字符串。如果为null或空集则返回空字符串；如果是数组则使用逗号分割后拼接；其它返回object.toString()结果。
     *
     * @param object 对象。
     * @return 字符串。
     */
    String toString(Object object);

    /**
     * 按指定格式将数值格式化为字符串。
     *
     * @param number 要进行格式化的数值。
     * @param format 目标格式。
     * @return 格式化后的数值字符串。
     */
    String toString(Number number, String format);

    /**
     * 将整数数值对象转化为指定精度的浮点数字符串。
     *
     * @param number  数值对象。
     * @param decimal 精确度。
     * @param point   保留的小数点位数。
     * @return 浮点数字符串。
     */
    String toString(Object number, int decimal, int point);

    /**
     * 将数组转化为字符串。
     *
     * @param array     数组。
     * @param separator 分隔符。
     * @return 字符串。
     */
    String toString(Object[] array, String separator);

    /**
     * 将可迭代的对象转化为字符串。
     *
     * @param iterable  可迭代的对象。
     * @param separator 分隔符。
     * @return 字符串。
     */
    String toString(Iterable<?> iterable, String separator);

    /**
     * 将集合转化为字符串。
     *
     * @param map       集合。
     * @param separator 分隔符。
     * @return 字符串。
     */
    String toString(Map<?, ?> map, String separator);

    /**
     * 将字符串按指定分隔符转化为字符串数组。
     *
     * @param string    要进行转化的字符串。
     * @param separator 分隔符。
     * @return 字符串数组。如果字符串为空则返回空数组；如果分隔符为null则返回仅包含一个元素、且为原字符串的数组。
     */
    String[] toArray(String string, String separator);

    /**
     * 将字符串按指定分隔符转化为二维数组。
     *
     * @param string    要转化的字符串。
     * @param separator 分隔符字符串数组，必须为两个元素的数组。
     * @return 转化后的二维数组；如果字符串为空或分隔符数组不为两个元素的数组则返回空二维数组。
     */
    String[][] toArray(String string, String[] separator);

    /**
     * 将数组转化为Set集。
     *
     * @param array 数组。
     * @param <T>   元素类。
     * @return Set集合；如果array为空则返回空集。
     */
    <T> Set<T> toSet(T[] array);

    /**
     * 将数值转化为使用K、M、G、T等单位表示的字符串。
     *
     * @param size 要转化的数值。
     * @return 使用K、M、G、T等单位表示的字符串，保留两位有效数字；如果数值为负数则返回空字符串。
     */
    String toBitSize(long size);

    /**
     * 将使用K、M、G、T等单位表示的字符串转化为整数值。
     *
     * @param size 使用K、M、G、T等单位表示的字符串。
     * @return 整数值；如果转化失败则返回-1。
     */
    long toBitSize(String size);

    /**
     * 将对象转化为boolean值。
     *
     * @param object 要转化的对象。
     * @return boolean值；如果转化失败则返回false。
     */
    boolean toBoolean(Object object);

    /**
     * 将字符串进行URL编码转换。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则使用默认编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    String encodeUrl(String string, String charset);

    /**
     * 将字符串进行URL解码。
     *
     * @param string  要转化的字符串。
     * @param charset 目标编码格式，如果为空则使用默认编码。
     * @return 转化后的字符串，如果转化失败将返回原字符串。
     */
    String decodeUrl(String string, String charset);

    /**
     * 将字符串转化为首字母小写的字符串。
     *
     * @param string 要转化的字符串。
     * @return 转化后的字符串；如果转化失败则返回原值。
     */
    String toFirstLowerCase(String string);

    /**
     * 将字符串转化为首字母大写的字符串。
     *
     * @param string 要转化的字符串。
     * @return 转化后的字符串；如果转化失败则返回原值。
     */
    String toFirstUpperCase(String string);

    /**
     * 将请求参数字符串转化为Map集合。
     *
     * @param parameters 参数字符串。
     * @return Map集合。
     */
    Map<String, String> toParameterMap(String parameters);
}
