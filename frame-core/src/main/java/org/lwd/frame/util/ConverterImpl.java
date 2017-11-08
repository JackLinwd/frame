package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author lwd
 */
@Component("frame.util.converter")
public class ConverterImpl implements Converter {
    private static final String[] BIT_SIZE_FORMAT = {"0 B", "0.00 K", "0.00 M", "0.00 G", "0.00 T"};

    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private DateTime dateTime;
    @Inject
    private Logger logger;

    @SuppressWarnings({"unchecked"})
    @Override
    public String toString(Object object) {
        if (validator.isEmpty(object))
            return "";

        if (object.getClass().isArray())
            return arrayString((Object[]) object, ",");

        if (object instanceof Iterable)
            return iterableString((Iterable<?>) object, ",");

        if (object instanceof Map)
            return mapString((Map<?, ?>) object, ",");

        if (object instanceof Date)
            return dateTime.toString((Date) object);

        return object.toString();
    }

    @Override
    public String toString(Number number, String format) {
        return numeric.toString(number, format);
    }

    @Override
    public String toString(Object number, int decimal, int point) {
        StringBuilder sb = new StringBuilder().append("0.");
        for (int i = 0; i < point; i++)
            sb.append('0');

        return toString(numeric.toLong(number) * Math.pow(0.1D, decimal), sb.toString());
    }

    @Override
    public String toString(Object[] array, String separator) {
        return validator.isEmpty(array) ? "" : arrayString(array, separator);
    }

    private String arrayString(Object[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Object object : array)
            sb.append(separator).append(toString(object));

        return sb.substring(separator.length());
    }

    @Override
    public String toString(Iterable<?> iterable, String separator) {
        return validator.isEmpty(iterable) ? "" : iterableString(iterable, separator);
    }

    private String iterableString(Iterable<?> iterable, String separator) {
        StringBuilder sb = new StringBuilder();
        iterable.forEach(obj -> sb.append(separator).append(toString(obj)));

        return sb.substring(separator.length());
    }

    @Override
    public String toString(Map<?, ?> map, String separator) {
        return validator.isEmpty(map) ? "" : mapString(map, separator);
    }

    private String mapString(Map<?, ?> map, String separator) {
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> sb.append(separator).append(toString(key)).append('=').append(toString(value)));

        return sb.substring(separator.length());
    }

    @Override
    public String[] toArray(String string, String separator) {
        if (string == null)
            return new String[0];

        if (separator == null || !string.contains(separator))
            return new String[]{string};

        List<String> list = new ArrayList<>();
        int length = separator.length();
        int index = 0;
        for (int indexOf; index <= (indexOf = string.indexOf(separator, index)); index = indexOf + length) {
            list.add(string.substring(index, indexOf));
        }
        if (index <= string.length())
            list.add(string.substring(index));

        return list.toArray(new String[0]);
    }

    @Override
    public String[][] toArray(String string, String[] separator) {
        if (validator.isEmpty(string) || validator.isEmpty(separator) || separator.length < 2 || validator.isEmpty(separator[0])
                || validator.isEmpty(separator[1]) || !string.contains(separator[1]))
            return new String[0][0];

        List<String> list = new ArrayList<>();
        for (String str : toArray(string, separator[0]))
            if (string.contains(separator[1]))
                list.add(str);

        if (list.isEmpty())
            return new String[0][0];

        String[][] array = new String[list.size()][];
        for (int i = 0; i < array.length; i++)
            array[i] = toArray(list.get(i), separator[1]);

        return array;
    }

    @Override
    public <T> Set<T> toSet(T[] array) {
        Set<T> set = new HashSet<>();
        if (array != null)
            Collections.addAll(set, array);

        return set;
    }

    @Override
    public String toBitSize(long size) {
        return toBitSize(size < 0 ? 0 : size, 0);
    }

    private String toBitSize(double size, int pattern) {
        if (size >= 1024 && pattern < BIT_SIZE_FORMAT.length - 1)
            return toBitSize(size / 1024, pattern + 1);

        return toString(size, BIT_SIZE_FORMAT[pattern]);
    }

    @Override
    public long toBitSize(String size) {
        if (validator.isEmpty(size))
            return -1L;

        double value = numeric.toDouble(size.substring(0, size.length() - 1).trim(), -1.0D);
        char unit = size.toLowerCase().charAt(size.length() - 1);
        if (unit == 't')
            return Math.round(value * 1024 * 1024 * 1024 * 1024);

        if (unit == 'g')
            return Math.round(value * 1024 * 1024 * 1024);

        if (unit == 'm')
            return Math.round(value * 1024 * 1024);

        if (unit == 'k')
            return Math.round(value * 1024);

        return Math.round(numeric.toDouble(size.trim(), -1.0D));
    }

    @Override
    public boolean toBoolean(Object object) {
        if (validator.isEmpty(object))
            return false;

        try {
            return Boolean.parseBoolean(object.toString());
        } catch (Exception e) {
            logger.warn(e, "将对象[{}]转化为布尔值时发生异常！", object);

            return false;
        }
    }

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
    public String toFirstLowerCase(String string) {
        return toFirstCase(string, 'A', 'Z', 'a' - 'A');
    }

    @Override
    public String toFirstUpperCase(String string) {
        return toFirstCase(string, 'a', 'z', 'A' - 'a');
    }

    private String toFirstCase(String string, char start, char end, int shift) {
        if (validator.isEmpty(string))
            return string;

        char ch = string.charAt(0);
        if (ch < start || ch > end)
            return string;

        char[] chars = string.toCharArray();
        chars[0] += shift;

        return new String(chars);
    }

    @Override
    public Map<String, String> toParameterMap(String parameters) {
        Map<String, String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder("&").append(parameters);
        String value = "";
        for (int indexOf; (indexOf = sb.lastIndexOf("&")) > -1; ) {
            String string = sb.substring(indexOf + 1);
            sb.delete(indexOf, sb.length());
            if ((indexOf = string.indexOf('=')) == -1) {
                value = "&" + string + value;

                continue;
            }
            map.put(string.substring(0, indexOf), decodeUrl(string.substring(indexOf + 1) + value, null));
            value = "";
        }

        return map;
    }
}
