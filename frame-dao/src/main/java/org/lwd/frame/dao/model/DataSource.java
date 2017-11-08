package org.lwd.frame.dao.model;

import java.lang.annotation.*;

/**
 * 数据源配置。
 *
 * @author lwd
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    /**
     * 数据源配置key。
     *
     * @return 数据源配置key。
     */
    String key();
}
