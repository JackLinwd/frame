package org.lwd.frame.bean;

import java.util.Collection;

/**
 * @author lwd
 */
public interface Container {
    /**
     * 获取Bean实例。
     *
     * @param <T>   目标类。
     * @param clazz 目标类型。
     * @return Bean实例；如果存在多个将随机返回一个，如果不存在将返回null。
     */
    <T> T getBean(Class<T> clazz);

    /**
     * 获取Bean实例。
     *
     * @param beanName Bean名称。
     * @return Bean实例；如果不存在将返回null。
     */
    <T> T getBean(String beanName);

    /**
     * 获取Bean实例。
     *
     * @param <T>      目标类。
     * @param beanName Bean定义名称。
     * @param clazz    目标类型。
     * @return Bean实例，如果不存在则返回null。
     */
    <T> T getBean(String beanName, Class<T> clazz);

    /**
     * 获取Bean实例集。
     *
     * @param <T>   目标类。
     * @param clazz 目标类型。
     * @return Bean实例集，如果不存在则返回空集。
     */
    <T> Collection<T> getBeans(Class<T> clazz);

    /**
     * 获取Bean类。
     *
     * @param beanName Bean定义名称。
     * @return Bean类。
     */
    <T> Class<T> getBeanClass(String beanName);

    /**
     * 获取所有定义的Bean名称集。
     *
     * @return Bean名称集。
     */
    String[] getBeanNames();

    /**
     * 映射Bean名称。
     *
     * @param beanName        bean名称。
     * @param dynamicBeanName 新bean名称。
     */
    void mapBeanName(String beanName, String dynamicBeanName);
}
