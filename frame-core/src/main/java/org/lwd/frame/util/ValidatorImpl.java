package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author lwd
 */
@Component("frame.util.validator")
public class ValidatorImpl implements Validator {
    private static final String EMAIL = "^(?:\\w+\\.?-?)*\\w+@(?:\\w+\\.?-?)*\\w+$";
    private static final String MOBILE = "^1\\d{10}$";

    private Map<String, Pattern> patterns = new ConcurrentHashMap<>();

    @Override
    public boolean isEmpty(Object object) {
        if (object == null)
            return true;

        if (object instanceof String)
            return ((String) object).trim().length() == 0;

        if (object.getClass().isArray())
            return Array.getLength(object) == 0;

        if(object instanceof Iterable)
            return !((Iterable<?>)object).iterator().hasNext();

        if (object instanceof Map)
            return ((Map<?, ?>) object).isEmpty();

        return false;
    }

    @Override
    public boolean isEmail(String email) {
        return isMatchRegex(EMAIL, email);
    }

    @Override
    public boolean isMobile(String mobile) {
        return isMatchRegex(MOBILE, mobile);
    }

    @Override
    public boolean isMatchRegex(String regex, String string) {
        return regex != null && string != null && getPattern(regex).matcher(string).matches();
    }

    private Pattern getPattern(String regex) {
        return patterns.computeIfAbsent(regex, Pattern::compile);
    }

    @Override
    public boolean isImage(String contentType, String name) {
        int indexOf;
        if (isEmpty(contentType) || isEmpty(name) || !contentType.startsWith("image/") || (indexOf = name.lastIndexOf('.')) == -1)
            return false;

        String suffix = name.substring(indexOf);
        return ((contentType.equals("image/jpeg") && (suffix.equals(".jpg") || suffix.equals(".jpeg"))) ||
                (contentType.equals("image/png") && suffix.equals(".png")) || (contentType.equals("image/gif") && suffix.equals(".gif")));
    }
}
