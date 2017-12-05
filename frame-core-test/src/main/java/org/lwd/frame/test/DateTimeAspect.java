package org.lwd.frame.test;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author lwd
 */
public interface DateTimeAspect {
    /**
     * 设置当前时间集。
     *
     * @param nows 当前时间集。
     */
    void now(List<Timestamp> nows);
}
