package org.lwd.frame.dao.test;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.springframework.stereotype.Repository;

/**
 * @author lwd
 */
@Repository("frame.test.tester.page")
public class PageTesterImpl implements PageTester {
    @Override
    public void assertCountSizeNumber(int count, int size, int number, JSONObject object) {
        Assert.assertEquals(count, object.getIntValue("count"));
        Assert.assertEquals(size, object.getIntValue("size"));
        Assert.assertEquals(number, object.getIntValue("number"));
    }
}
