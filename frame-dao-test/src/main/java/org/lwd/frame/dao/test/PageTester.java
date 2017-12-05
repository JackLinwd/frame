package org.lwd.frame.dao.test;

import com.alibaba.fastjson.JSONObject;

/**
 * @author lwd
 */
public interface PageTester {
    /**
     * 测试分页记录总数、每页记录数、当前页数。
     *
     * @param count  目标记录总数。
     * @param size   目标每页记录数。
     * @param number 目标当前页数。
     * @param object 包含分页信息的JSON数据。
     */
    void assertCountSizeNumber(int count, int size, int number, JSONObject object);
}
