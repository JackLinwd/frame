package org.lwd.frame.dao.model;

import java.lang.annotation.*;

/**
 * 内存表定义。
 *
 * @author lwd
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Memory {
    /**
     * 内存表名称。
     *
     * @return 内存表名称。
     */
    String name();
}
