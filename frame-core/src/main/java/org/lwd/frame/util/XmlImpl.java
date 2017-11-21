package org.lwd.frame.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by linwd on 2016/10/8.
 */
@Component("frame.util.xml")
public class XmlImpl implements Xml {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private Json json;

    @Override
    public JSONObject toJson(String xml) {
        if (validator.isEmpty(xml))
            return null;
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            logger.error(e, "xml转json，格式化出错");
        }
        JSONObject json = new JSONObject();
        if (doc == null) return json;
        dom4j2Json(doc.getRootElement(), json);
        return json;
    }

    private void dom4j2Json(Element element, JSONObject json) {
        //如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (!validator.isEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && !validator.isEmpty(element.getText())) {//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for (Element e : chdEl) {//有子元素
            if (!e.elements().isEmpty()) {//子元素也有子元素
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {//如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }


            } else {//子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (!validator.isEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

    @Override
    public Map<String, String> toMap(String xml, boolean root) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            logger.error(e, "xml转json，格式化出错");
        }
        if (doc == null) return new HashMap<String, String>();
        Element rootElement = doc.getRootElement();
        Map<String, String> map = (Map<String, String>) xml2map(rootElement);
        if (rootElement.elements().size() == 0 && rootElement.attributes().size() == 0) {
            return map;
        }
        if (root) {
            //在返回的map里加根节点键（如果需要）
            Map<String, String> rootMap = new HashMap<String, String>();
            rootMap.put(rootElement.getName(), map.toString());
            return rootMap;
        }
        return map;
    }

    /**
     * xml转map 不带属性
     *
     * @param e
     * @return
     */
    private static Map xml2map(Element e) {
        Map map = new LinkedHashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }
}
