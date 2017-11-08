package org.lwd.frame.bean;

import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 * @author lwd
 */
@Component("frame.bean.container")
public class ContainerImpl implements Container, ApplicationListener<ApplicationEvent>, ApplicationContextAware {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private Optional<List<ContextRefreshedListener>> refreshedListeners;
    @Inject
    private Optional<List<ContextClosedListener>> closedListeners;
    private ApplicationContext applicationContext;
    private Map<String, String> map = new HashMap<>();
    private Set<String> set = new HashSet<>();

    @Override
    public <T> T getBean(Class<T> clazz) {
        Collection<T> beans = getBeans(clazz);

        return beans.isEmpty() ? null : beans.iterator().next();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName) {
        if (validator.isEmpty(beanName))
            return null;

        try {
            return (T) applicationContext.getBean(getBeanName(beanName));
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public <T> T getBean(String beanName, Class<T> clazz) {
        if (validator.isEmpty(beanName))
            return null;

        try {
            return applicationContext.getBean(getBeanName(beanName), clazz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private String getBeanName(String beanName) {
        String dynamicBeanName = map.get(beanName);

        return dynamicBeanName == null ? beanName : dynamicBeanName;
    }

    @Override
    public <T> Collection<T> getBeans(Class<T> clazz) {
        Map<String, T> map = applicationContext.getBeansOfType(clazz);
        set.forEach(map::remove);

        return map.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Class<T> getBeanClass(String beanName) {
        if (validator.isEmpty(beanName))
            return null;

        try {
            return (Class<T>) applicationContext.getType(getBeanName(beanName));
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public String[] getBeanNames() {
        return applicationContext.getBeanDefinitionNames();
    }

    @Override
    public void mapBeanName(String beanName, String dynamicBeanName) {
        set.add(beanName);
        if (map.containsKey(beanName))
            set.add(map.get(beanName));

        map.put(beanName, dynamicBeanName);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent)
            onContextRefreshed();
        else if (event instanceof ContextClosedEvent)
            onContextClosed();
    }

    private void onContextRefreshed() {
        if (logger.isDebugEnable())
            logger.debug("开始执行Bean环境初始化完成后续工作。");

        if (!refreshedListeners.isPresent()) {
            if (logger.isInfoEnable())
                logger.info("无需执行Bean环境初始化完成后续工作。");

            return;
        }

        refreshedListeners.get().sort(Comparator.comparingInt(ContextRefreshedListener::getContextRefreshedSort));
        refreshedListeners.get().forEach(listener -> {
            try {
                listener.onContextRefreshed();
            } catch (Throwable e) {
                logger.warn(e, "执行Bean[{}:{}]环境初始化时发生异常！", listener.getContextRefreshedSort(), listener);
            }
        });

        if (logger.isInfoEnable())
            logger.info("执行[{}]个Bean环境初始化完成后续工作。", refreshedListeners.get().size());
    }

    private void onContextClosed() {
        if (logger.isDebugEnable())
            logger.debug("开始执行Bean环境关闭后续工作。");

        if (!closedListeners.isPresent()) {
            if (logger.isInfoEnable())
                logger.info("无需执行Bean环境关闭后续工作。");

            return;
        }

        closedListeners.get().sort(Comparator.comparingInt(ContextClosedListener::getContextClosedSort));
        closedListeners.get().forEach(listener -> {
            try {
                listener.onContextClosed();
            } catch (Throwable e) {
                logger.warn(e, "执行Bean[{}:{}]环境关闭时发生异常！", listener.getContextClosedSort(), listener);
            }
        });

        if (logger.isInfoEnable())
            logger.info("执行[{}]个Bean环境关闭后续工作。", closedListeners.get().size());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
