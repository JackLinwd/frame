package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;

import java.awt.*;
import java.io.IOException;

/**
 * 解析器支持。
 *
 * @author lwd
 */
public interface ParserHelper {
    /**
     * 获取解析器。
     *
     * @param type 解析器类型。
     * @return 解析器；如果不存在则返回null。
     */
    Parser get(String type);

    /**
     * 获取矩形对象。
     *
     * @param object JSON数据。
     * @return 矩形对象。
     */
    Rectangle getRectangle(JSONObject object);

    /**
     * 旋转。
     *
     * @param xslfSimpleShape XSLFSimpleShape实例。
     * @param object          JSON数据。
     */
    void rotate(XSLFSimpleShape xslfSimpleShape, JSONObject object);

    /**
     * 获取颜色对象。
     *
     * @param object JSON数据。
     * @param key    颜色KEY。
     * @return 颜色对象；如果不存在或获取失败则返回null。
     */
    Color getColor(JSONObject object, String key);

    /**
     * 截取图片。
     *
     * @param data   图片数据。
     * @param object JSON数据。
     * @param format 输出图片格式。
     * @return 截取图片数据。
     * @throws IOException 未处理IOException。
     */
    byte[] subImage(byte[] data, JSONObject object, String format) throws IOException;
}
