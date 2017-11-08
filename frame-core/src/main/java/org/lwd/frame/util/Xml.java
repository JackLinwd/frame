package org.lwd.frame.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * XML操作。
 *
 * @author lwd
 */
public interface Xml {
    /**
     * 转化为JSON数据。
     *
     * @param xml XML数据。
     * @return JSON数据。
     */
    JSONObject toJson(String xml);

    /**
     * 转化为Map对象。
     *
     * @param xml  XML数据。
     * @param root 是否保存根节点，如果为true则显示根节点名称，否则忽略。
     * @return Map对象。
     */
    Map<String, String> toMap(String xml, boolean root);
}
