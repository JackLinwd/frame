package org.lwd.frame.util;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("frame.util.date-time")
public class DateTimeImpl implements DateTime {
    @Inject
    private Validator validator;
    @Inject
    private Message message;
    @Inject
    private Context context;
    @Inject
    private Logger logger;
    private Map<String, FastDateFormat> dateFormatMap = new ConcurrentHashMap<>();
    private Map<Locale, String> dateFormat = new ConcurrentHashMap<>();
    private Map<Locale, String> dateTimeFormat = new ConcurrentHashMap<>();

    @Override
    public java.sql.Date today() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    @Override
    public Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    public Timestamp getStart(Date date) {
        return getStart(date, false);
    }

    @Override
    public Timestamp getStart(Date date, boolean month) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        if (month)
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Timestamp(calendar.getTime().getTime());
    }

    @Override
    public Timestamp getEnd(Date date) {
        return getEnd(date, false);
    }

    @Override
    public Timestamp getEnd(Date date, boolean month) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        if (month) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            calendar.set(Calendar.DAY_OF_MONTH, 0);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return new Timestamp(calendar.getTime().getTime());
    }

    @Override
    public Timestamp getStart(String string) {
        return getStart(string, false);
    }

    @Override
    public Timestamp getStart(String string, boolean month) {
        return getStart(toDate(string), month);
    }

    @Override
    public Timestamp getEnd(String string) {
        return getEnd(string, false);
    }

    @Override
    public Timestamp getEnd(String string, boolean month) {
        return getEnd(toDate(string), month);
    }

    @Override
    public Timestamp toTime(String string) {
        return toTimestamp(toDate(string));
    }

    @Override
    public Timestamp toTime(String string, String pattern) {
        return toTimestamp(toDate(string, pattern));
    }

    private Timestamp toTimestamp(Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }

    @Override
    public String toString(Date date, String format) {
        return date == null ? "" : getDateFormat(format).format(date);
    }

    @Override
    public String toString(Date date) {
        return toString(date, date instanceof Timestamp ? getDateTimeFormat() : getDateFormat());
    }

    @Override
    public Date toDate(Object date) {
        if (validator.isEmpty(date))
            return null;

        if (date instanceof Date)
            return (Date) date;

        if (date instanceof String) {
            String dateFormat = getDateFormat();
            String string = (String) date;

            return toDate(string, string.length() == dateFormat.length() ? dateFormat : getDateTimeFormat());
        }

        return null;
    }

    private String getDateFormat() {
        Locale locale = context.getLocale();

        return dateFormat.computeIfAbsent(locale, l -> message.get("tephra.format.date"));
    }

    private String getDateTimeFormat() {
        Locale locale = context.getLocale();

        return dateTimeFormat.computeIfAbsent(locale, l -> message.get("tephra.format.date-time"));
    }

    @Override
    public Date toDate(String date, String format) {
        if (validator.isEmpty(date) || validator.isEmpty(format) || date.length() != format.length())
            return null;

        try {
            return getDateFormat(format).parse(date);
        } catch (ParseException e) {
            logger.warn(e, "使用格式[{}]将字符串[{}]转化为日期值时发生异常！", format, date);

            return null;
        }
    }

    private FastDateFormat getDateFormat(String format) {
        return dateFormatMap.computeIfAbsent(format, FastDateFormat::getInstance);
    }

    @Override
    public long toLong(Date date) {
        return date == null ? 0L : date.getTime();
    }
}
