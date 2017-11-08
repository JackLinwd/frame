package org.lwd.frame.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 日期时间工具。
 *
 * @author lwd
 */
public interface DateTime {
    /**
     * 获取当前日期。
     *
     * @return 当前日期。
     */
    java.sql.Date today();

    /**
     * 获取当前时间。
     *
     * @return 当前时间。
     */
    Timestamp now();

    /**
     * 获取指定日期当天的开始时间，即当天的00:00:00.000。
     *
     * @param date 目标日期。
     * @return 开始时间；如果获取失败则返回null。
     */
    Timestamp getStart(Date date);

    /**
     * 获取指定日期当天的开始时间，即当天的00:00:00.000。
     *
     * @param date  目标日期。
     * @param month 是否月份，如果是则将日期设置为当月第一天。
     * @return 开始时间；如果获取失败则返回null。
     */
    Timestamp getStart(Date date, boolean month);

    /**
     * 获取指定日期当天的结束时间，即当天的23:59:59.999。
     *
     * @param date 目标日期。
     * @return 结束时间；如果获取失败则返回null。
     */
    Timestamp getEnd(Date date);

    /**
     * 获取指定日期当天的结束时间，即当天的23:59:59.999。
     *
     * @param date  目标日期。
     * @param month 是否月份，如果是则将日期设置为当月最后一天。
     * @return 结束时间；如果获取失败则返回null。
     */
    Timestamp getEnd(Date date, boolean month);

    /**
     * 获取日期（时间）字符串当天的开始时间，即当天的00:00:00.000。
     *
     * @param string 日期（时间）字符串。
     * @return 开始时间；如果获取失败则返回null。
     */
    Timestamp getStart(String string);

    /**
     * 获取日期（时间）字符串当天的开始时间，即当天的00:00:00.000。
     *
     * @param string 日期（时间）字符串。
     * @param month  是否月份，如果时则将日期设置为当月第一天。
     * @return 开始时间；如果获取失败则返回null。
     */
    Timestamp getStart(String string, boolean month);

    /**
     * 获取日期（时间）字符串当天的结束时间，即当天的23:59:59.999。
     *
     * @param string 日期（时间）字符串。
     * @return 结束时间；如果获取失败则返回null。
     */
    Timestamp getEnd(String string);

    /**
     * 获取日期（时间）字符串当天的结束时间，即当天的23:59:59.999。
     *
     * @param string 日期（时间）字符串。
     * @param month  是否月份，如果时则将日期设置为当月最后一天。
     * @return 结束时间；如果获取失败则返回null。
     */
    Timestamp getEnd(String string, boolean month);

    /**
     * 将日期时间字符串转化为时间戳。
     *
     * @param string 字符串。
     * @return 时间戳；如果转换失败则返回null。
     */
    Timestamp toTime(String string);

    /**
     * 将日期时间字符串转化为时间戳。
     *
     * @param string  字符串。
     * @param pattern 日期时间格式。
     * @return 时间戳；如果转换失败则返回null。
     */
    Timestamp toTime(String string, String pattern);

    /**
     * 按指定格式将日期值格式化为字符串。
     *
     * @param date   要进行格式化的日期值。
     * @param format 目标格式。
     * @return 格式化后的日期值字符串。
     */
    String toString(Date date, String format);

    /**
     * 将日期值格式化为字符串。
     *
     * @param date 要进行格式化的日期值。
     * @return 格式化后的日期值字符串。
     */
    String toString(Date date);

    /**
     * 使用默认格式将日期对象转化为日期值。
     *
     * @param date 日期对象。
     * @return 日期值。如果格式不匹配则返回null。
     */
    Date toDate(Object date);

    /**
     * 将日期字符串按指定格式转化为日期值。
     *
     * @param date   日期字符串。
     * @param format 字符串格式。
     * @return 日期值。如果格式不匹配则返回null。
     */
    Date toDate(String date, String format);

    /**
     * 获取日期毫秒值。
     *
     * @param date 日期值。
     * @return 毫秒值，如果日期值为null则返回0L。
     */
    long toLong(Date date);
}
