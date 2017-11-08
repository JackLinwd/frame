package org.lwd.frame.ctrl.validate;

import java.lang.annotation.*;

/**
 * 验证规则集。
 *
 * @author lwd
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validates {
    /**
     * 验证规则集。
     *
     * @return 验证规则集。
     */
    Validate[] value();
}
