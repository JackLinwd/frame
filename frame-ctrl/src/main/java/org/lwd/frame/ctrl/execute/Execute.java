package org.lwd.frame.ctrl.execute;

import org.lwd.frame.ctrl.validate.Validate;

import java.lang.annotation.*;

/**
 * 执行器。用于标注执行服务。
 *
 * @author lwd
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Execute {
    /**
     * 服务名称。支持使用正则表达式；多个名称间以逗号分隔。
     *
     * @return 服务名称。
     */
    String name();

    /**
     * 资源key。
     *
     * @return 资源key。
     */
    String key() default "";

    /**
     * 验证规则集。
     *
     * @return 验证规则集。
     */
    Validate[] validates() default {};

    /**
     * 输出类型。
     *
     * @return 输出类型。
     */
    String type() default "";

    /**
     * 输出模版。
     *
     * @return 输出模版。
     */
    String template() default "";

    /**
     * 错误编码。
     *
     * @return 错误编码。
     */
    String code() default "";
}
