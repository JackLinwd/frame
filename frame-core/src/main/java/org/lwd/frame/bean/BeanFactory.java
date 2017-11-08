package org.lwd.frame.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Bean实例工厂，用于获取Bean实例。
 *
 * @author lwd
 */
@Component("frame.bean.factory")
public class BeanFactory implements ApplicationContextAware {
    protected static Container container;

    /**
     * 获取Bean实例。
     *
     * @param <T>   目标类。
     * @param clazz 目标类型。
     * @return Bean实例；如果存在多个将随机返回一个，如果不存在将返回null。
     */
    public static <T> T getBean(Class<T> clazz) {
        return container.getBean(clazz);
    }

    /**
     * 获取Bean实例。
     *
     * @param beanName Bean名称。
     * @return Bean实例；如果不存在将返回null。
     */
    public static <T> T getBean(String beanName) {
        return container.getBean(beanName);
    }

    /**
     * 获取Bean实例。
     *
     * @param <T>      目标类。
     * @param beanName Bean定义名称。
     * @param clazz    目标类型。
     * @return Bean实例，如果不存在则返回null。
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return container.getBean(beanName, clazz);
    }

    /**
     * 获取Bean实例集。
     *
     * @param <T>   目标类。
     * @param clazz 目标类型。
     * @return Bean实例集，如果不存在则返回空集。
     */
    public static <T> Collection<T> getBeans(Class<T> clazz) {
        return container.getBeans(clazz);
    }

    /**
     * 获取Bean类。
     *
     * @param beanName Bean定义名称。
     * @return Bean类。
     */
    public static <T> Class<T> getBeanClass(String beanName) {
        return container.getBeanClass(beanName);
    }

    /**
     * 获取所有定义的Bean名称集。
     *
     * @return Bean名称集。
     */
    public static String[] getBeanNames() {
        return container.getBeanNames();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        container = applicationContext.getBean(Container.class);
    }
}
