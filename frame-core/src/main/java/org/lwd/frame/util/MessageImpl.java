package org.lwd.frame.util;

import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lwd
 */
@Component("frame.util.message")
public class MessageImpl implements Message, ContextRefreshedListener {
    @Inject
    private Context context;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    private ResourceBundleMessageSource messageSource;

    @Override
    public String get(String key, Object... args) {
        return messageSource.getMessage(key, args, key, context.getLocale());
    }

    @Override
    public String[] getAsArray(String key, Object... args) {
        return converter.toArray(get(key, args), ",");
    }

    @Override
    public int getContextRefreshedSort() {
        return 1;
    }

    @Override
    public void onContextRefreshed() {
        Set<String> messages = new HashSet<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for (String beanName : BeanFactory.getBeanNames()) {
            Package beanPackage = BeanFactory.getBeanClass(beanName).getPackage();
            if (beanPackage == null) {
                logger.warn(null, "无法获得Bean[{}]包。", beanName);

                continue;
            }

            String packageName = beanPackage.getName();
            if (resolver.getResource(packageName.replace('.', File.separatorChar) + "/message.properties").exists())
                messages.add(packageName);
        }

        String[] names = new String[messages.size()];
        int i = 0;
        for (String name : messages)
            names[i++] = name + ".message";
        messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding(context.getCharset(null));
        messageSource.setBasenames(names);
    }
}
