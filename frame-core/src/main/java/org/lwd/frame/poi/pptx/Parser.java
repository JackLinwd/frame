package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lwd.frame.poi.StreamWriter;

/**
 * PPTx解析器。
 *
 * @author lwd
 */
public interface Parser {
    /**
     * 文本解析器类型。
     */
    String TYPE_TEXT = "text";
    /**
     * 图片解析器类型。
     */
    String TYPE_IMAGE = "image";
    /**
     * SVG图片解析器类型。
     */
    String TYPE_SVG = "svg";

    /**
     * 获取解析器类型。
     *
     * @return 解析器类型。
     */
    String getType();

    /**
     * 解析数据并添加到PPTx元素。
     *
     * @param xmlSlideShow PPTx实例。
     * @param xslfSlide    Slide实例。
     * @param object       数据。
     * @return 如果解析成功则返回true；否则返回false。
     */
    boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object);

    /**
     * 解析PPTx元素数据。
     *
     * @param object       解析后的数据。
     * @param xslfShape    要解析的PPTx元素。
     * @param streamWriter 流数据写入器。
     * @return 如果解析成功则返回true；否则返回false。
     */
    boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter streamWriter);
}
