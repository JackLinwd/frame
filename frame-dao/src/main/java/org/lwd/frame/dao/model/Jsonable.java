package org.lwd.frame.dao.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义可转化为JSON的属性。
 *
 * @author lwd
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Jsonable {
    /**
     * 数据格式。
     *
     * @return 数据格式。
     */
    String format() default "";

    /**
     * 是否为扩展属性。
     *
     * @return 是否为扩展属性。
     */
    boolean extend() default false;
}
