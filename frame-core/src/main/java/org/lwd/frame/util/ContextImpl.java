package org.lwd.frame.util;

import org.lwd.frame.bean.ContextRefreshedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwd
 */
@Component("frame.util.context")
public class ContextImpl implements Context, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Thread thread;
    @Inject
    private Logger logger;
    @Value("${frame.util.context.charset:UTF-8}")
    private String charset;
    private String root;
    private Map<String, String> map = new ConcurrentHashMap<>();
    private ThreadLocal<Locale> locale = new ThreadLocal<>();

    @Override
    public String getAbsolutePath(String path) {
        String absolutePath = map.get(path);
        if (absolutePath == null) {
            if (path.startsWith("abs:"))
                absolutePath = path.substring(4);
            else if (path.startsWith("classpath:"))
                absolutePath = getClass().getClassLoader().getResource(path.substring(10)).getPath();
            else
                absolutePath = new File(root + "/" + path).getAbsolutePath();
            map.put(path, absolutePath);
        }

        return absolutePath;
    }

    @Override
    public String getCharset(String charset) {
        return validator.isEmpty(charset) ? this.charset : charset;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    @Override
    public Locale getLocale() {
        Locale locale = this.locale.get();
        if (locale == null)
            locale = Locale.getDefault();

        return locale;
    }

    @Override
    public int getContextRefreshedSort() {
        return 1;
    }

    @Override
    public void onContextRefreshed() {
        String path = getClass().getResource("/").getPath();
        for (String name : new String[]{"/classes/", "/WEB-INF/"}) {
            int indexOf = path.lastIndexOf(name);
            if (indexOf > -1)
                path = path.substring(0, indexOf + 1);
        }

        root = path.replace(File.separatorChar, '/');
        map.clear();

        if (logger.isInfoEnable())
            logger.info("设置运行期根路径：{}", root);
    }
}
