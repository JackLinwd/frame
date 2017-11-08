package org.lwd.frame.ctrl.validate;

import java.lang.annotation.*;

/**
 * 验证规则。
 *
 * @author lwd
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    /**
     * 验证范围。
     */
    enum Scope {
        /**
         * 请求头。
         */
        Header,
        /**
         * 请求参数。
         */
        Request
    }

    /**
     * 验证器Bean名称。
     *
     * @return 验证器Bean名称。
     */
    String validator();

    /**
     * 验证范围。
     *
     * @return 验证范围。
     */
    Scope scope() default Scope.Request;

    /**
     * 验证参数名。
     *
     * @return 验证参数名。
     */
    String parameter() default "";

    /**
     * 验证参数名集；同时验证多个参数，或多个参数之间进行关联验证。
     *
     * @return 验证参数名集。
     */
    String[] parameters() default {};

    /**
     * 是否允许为空。
     *
     * @return 是否允许为空。
     */
    boolean emptyable() default false;

    /**
     * 整数配置值数组。用于设置验证规则需要的整数参数值。
     *
     * @return 整数配置值数组。
     */
    int[] number() default {};

    /**
     * 字符串配置值数组。用于设置验证规则需要的字符串参数值。
     *
     * @return 字符串配置值数组。
     */
    String[] string() default {};

    /**
     * 验证失败错误编码。
     *
     * @return 验证失败错误编码。
     */
    int failureCode() default 0;

    /**
     * 验证失败错误信息资源key。
     *
     * @return 验证失败错误信息资源key。
     */
    String failureKey() default "";

    /**
     * 验证失败错误信息参数资源key。
     *
     * @return 验证失败错误信息参数资源key。
     */
    String[] failureArgKeys() default {};
}
