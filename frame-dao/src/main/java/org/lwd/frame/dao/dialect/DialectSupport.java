package org.lwd.frame.dao.dialect;

/**
 * 数据库方言支持。
 *
 * @author lwd
 */
public abstract class DialectSupport implements Dialect {
    @Override
    public String getValidationQuery() {
        return "SELECT 1";
    }

    @Override
    public String getLike(String like, boolean prefix, boolean suffix) {
        if (like == null)
            return prefix || suffix ? "%" : "";

        StringBuilder sb = new StringBuilder();
        if (prefix)
            sb.append('%');
        for (char ch : like.toCharArray()) {
            if (ch == '%')
                sb.append("[%]");
            else
                sb.append(ch);
        }
        if (suffix)
            sb.append('%');

        return sb.toString();
    }
}
