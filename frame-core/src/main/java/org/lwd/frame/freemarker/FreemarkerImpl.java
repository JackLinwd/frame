package org.lwd.frame.freemarker;

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
            getConfiguration().getTemplate(name + suffix).process(BeanFactory.getBean(Model.class).setData(data),
                    new OutputStreamWriter(output));
        } catch (Exception e) {
            logger.warn(e, "解析模版[{}]时发生异常！", name);
        }
    }

    private synchronized Configuration getConfiguration() throws IOException {
        if (configuration == null) {
            configuration = new Configuration(Configuration.VERSION_2_3_27);
            configuration.setDirectoryForTemplateLoading(new File(context.getAbsolutePath(root)));
            configuration.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_27));
            configuration.setTemplateExceptionHandler((e, env, out) -> logger.warn(e, "解析FreeMarker模板时发生异常！"));
        }

        return configuration;
    }
}
