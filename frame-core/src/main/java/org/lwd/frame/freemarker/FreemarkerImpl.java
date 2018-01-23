package org.lwd.frame.freemarker;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateException;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.util.Context;
import org.lwd.frame.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.*;

/**
 * @author lwd
 */
@Component("frame.freemarker")
public class FreemarkerImpl implements Freemarker {
    @Inject
    private Context context;
    @Inject
    private Logger logger;
    @Value("${frame.freemarker.root:/WEB-INF/ftl}")
    private String root;
    @Value("${frame.freemarker.suffix:.ftl}")
    private String suffix;
    private Configuration configuration;
    private StringTemplateLoader stringTemplateLoader;

    @Override
    public boolean containsStringTemplate(String name) {
        return stringTemplateLoader != null && stringTemplateLoader.findTemplateSource(name + suffix) != null;
    }

    @Override
    public void putStringTemplate(String name, String template) {
        try {
            if (stringTemplateLoader == null)
                getConfiguration();

            stringTemplateLoader.putTemplate(name + suffix, template);
            getConfiguration().removeTemplateFromCache(name + suffix);
        } catch (IOException e) {
            logger.warn(e, "设置字符串模板[{}:{}]时发生异常！", name, template);
        }
    }

    @Override
    public void removeStringTemplate(String name) {
        if (stringTemplateLoader == null)
            return;

        try {
            stringTemplateLoader.removeTemplate(name + suffix);
            getConfiguration().removeTemplateFromCache(name + suffix);
        } catch (IOException e) {
            logger.warn(e, "移除字符串模板[{}]时发生异常！", name);
        }
    }

    @Override
    public String process(String name, Object data) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            process(name, data, output);
            output.close();

            return output.toString();
        } catch (IOException e) {
            logger.warn(e, "解析模版[{}]时发生异常！", name);

            return null;
        }
    }

    @Override
    public void process(String name, Object data, OutputStream output) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(output);
            getConfiguration().getTemplate(name + suffix).process(BeanFactory.getBean(Model.class).setData(data), outputStreamWriter);
            output.close();
        } catch (Exception e) {
            logger.warn(e, "解析模版[{}]时发生异常！", name);
        }
    }

    private Configuration getConfiguration() throws IOException {
        if (configuration == null) {
            synchronized (this) {
                if (configuration == null) {
                    configuration = new Configuration(Configuration.VERSION_2_3_27);
                    configuration.setTemplateLoader(new MultiTemplateLoader(new TemplateLoader[]{
                            new FileTemplateLoader(new File(context.getAbsolutePath(root))),
                            stringTemplateLoader = new StringTemplateLoader()
                    }));
                    configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_27));
                    configuration.setTemplateExceptionHandler((e, env, out) -> logger.warn(e, "解析FreeMarker模板时发生异常！"));
                }
            }
        }

        return configuration;
    }
}
