package org.lwd.frame.bean;

import org.lwd.frame.storage.StorageListener;
import org.lwd.frame.storage.Storages;
import org.lwd.frame.util.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lwd
 */
@Component("frame.bean.class-reloader")
public class ClassReloaderImpl implements ClassReloader, StorageListener, ApplicationContextAware {
    @Inject
    private Converter converter;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;
    @Inject
    private Container container;
    @Value("${frame.bean.reload.class-path:}")
    private String classPath;
    private List<ClassLoader> loaders;
    private List<String> names;
    private Map<Class<?>, List<Injecter>> injecters;
    private ApplicationContext applicationContext;

    @Override
    public boolean isReloadEnable(String name) {
        return names.contains(name);
    }

    @Override
    public String getClassPath() {
        return context.getAbsolutePath(classPath);
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return validator.isEmpty(classPath) ? null : new String[]{classPath + "/name"};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        if ((names = names(absolutePath)) == null)
            return;

        if (logger.isInfoEnable())
            logger.info("重新载入类：{}", names);

        if (loaders == null) {
            loaders = new ArrayList<>();
            loaders.add(applicationContext.getClassLoader());
        }

        if (injecters == null) {
            injecters = new ConcurrentHashMap<>();

            for (String name : container.getBeanNames())
                inject(container.getBeanClass(name), name, null);
        }

        ClassLoader loader = new DynamicClassLoader(loaders.get(loaders.size() - 1));
        names.forEach((name) -> load(loader, name));
        loaders.add(loader);
    }

    private List<String> names(String absolutePath) {
        String names = io.readAsString(absolutePath).trim();
        if (validator.isEmpty(names))
            return null;

        List<String> list = new ArrayList<>();
        for (String name : converter.toArray(names, "\n"))
            if (name.trim().length() > 0)
                list.add(name.trim());

        io.write(absolutePath, new byte[0]);

        return list;
    }

    private void inject(Class<?> beanClass, String beanName, Object bean) {
        for (Field field : beanClass.getFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Inject.class) == null)
                continue;

            try {
                Class<?> key = field.getType();
                boolean collection = isCollection(key);
                if (collection) {
                    Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    if (type instanceof Class)
                        key = (Class<?>) type;
                    else
                        continue;
                }

                List<Injecter> list = injecters.get(key);
                if (list == null)
                    list = new ArrayList<>();
                list.add(new Injecter(bean == null ? container.getBean(beanName) : bean, field, collection));
                injecters.put(key, list);
            } catch (Exception e) {
                logger.warn(e, "解析[{}]属性[{}]依赖时发生异常！", beanClass, field.getName());
            }
        }
    }

    private boolean isCollection(Class<?> clazz) {
        try {
            return clazz.equals(clazz.asSubclass(Collection.class));
        } catch (Exception e) {
            return false;
        }
    }

    private void load(ClassLoader loader, String name) {
        try {
            DefaultListableBeanFactory lbf = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            BeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(null, name, loader);
            String dynamicBeanName = generator.uuid();
            lbf.registerBeanDefinition(dynamicBeanName, bd);
            Object bean = lbf.getBean(dynamicBeanName);
            String beanName = getBeanName(bean.getClass());
            Object oldBean = null;
            if (beanName != null) {
                oldBean = container.getBean(beanName);
                container.mapBeanName(beanName, dynamicBeanName);
            }
            inject(bean.getClass(), null, bean);
            inject(bean, oldBean);
        } catch (Exception e) {
            logger.warn(e, "重新载入[{}]时发生异常！", name);
        }
    }

    private String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (component != null)
            return component.value();

        Repository repository = clazz.getAnnotation(Repository.class);
        if (repository != null)
            return repository.value();

        Service service = clazz.getAnnotation(Service.class);
        if (service != null)
            return service.value();

        Controller controller = clazz.getAnnotation(Controller.class);
        if (controller != null)
            return controller.value();

        return null;
    }

    @SuppressWarnings("unchecked")
    private void inject(Object bean, Object oldBean) throws IllegalArgumentException, IllegalAccessException {
        for (Class<?> key : injecters.keySet()) {
            if (!key.isInstance(bean))
                continue;

            for (Injecter injecter : injecters.get(key)) {
                Object value = bean;
                if (injecter.isCollection()) {
                    Collection<Object> collection = (Collection<Object>) injecter.getField().get(injecter.getBean());
                    if (oldBean != null)
                        collection.remove(oldBean);
                    collection.add(bean);
                    value = collection;
                }
                injecter.getField().set(injecter.getBean(), value);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
