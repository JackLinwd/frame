package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
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
     * 获取画笔颜色值。
     *
     * @param paintStyle  画笔。
     * @param ignoreWhite 是否忽略白色，是且为白色时返回null。
     * @return 颜色16进制值；获取失败或忽略白色则返回null。
     */
    String getHexColor(PaintStyle paintStyle, boolean ignoreWhite);

    /**
     * 转换颜色对象为16进制值。
     *
     * @param color 颜色对象。
     * @return 颜色16进制值；转换失败则返回null。
     */
    String toHex(Color color);

    /**
     * 获取图片数据。
     *
     * @param object       JSON数据。
     * @param contentType  图片格式。
     * @param outputStream 图片数据。
     * @return 图片数据。
     * @throws IOException 未处理IOException。
     */
    byte[] getImage(JSONObject object, String contentType, ByteArrayOutputStream outputStream) throws IOException;
}
