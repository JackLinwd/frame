package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.lwd.frame.poi.pptx.Parser;
import org.lwd.frame.poi.pptx.ParserHelper;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author lpw
 */
@Component("tephra.poi.pptx")
public class PptxImpl implements Pptx {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public void write(JSONObject object, OutputStream outputStream) {
        if (validator.isEmpty(object) || !object.containsKey("slides"))
            return;

        XMLSlideShow xmlSlideShow = new XMLSlideShow();
        setSize(xmlSlideShow, object);
        slides(xmlSlideShow, object.getJSONArray("slides"));

        try {
            xmlSlideShow.write(outputStream);
        } catch (IOException e) {
            logger.warn(e, "输出PPTx到输出流时发生异常！");
        }
    }

    private void setSize(XMLSlideShow xmlSlideShow, JSONObject object) {
        if (!object.containsKey("size"))
            return;

        JSONObject size = object.getJSONObject("size");
        if (size.getIntValue("width") <= 0 || size.getIntValue("height") <= 0)
            return;

        xmlSlideShow.setPageSize(new Dimension(size.getIntValue("width"), size.getIntValue("height")));
    }

    private void slides(XMLSlideShow xmlSlideShow, JSONArray slides) {
        for (int i = 0, size = slides.size(); i < size; i++)
            elements(xmlSlideShow, xmlSlideShow.createSlide(), slides.getJSONObject(i).getJSONArray("elements"));
    }

    private void elements(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONArray elements) {
        for (int i = 0, size = elements.size(); i < size; i++) {
            JSONObject element = elements.getJSONObject(i);
            if (!element.containsKey("type"))
                continue;

            Parser parser = parserHelper.get(element.getString("type"));
            if (parser != null)
                parser.parse(xmlSlideShow, xslfSlide, element);
        }
    }

    @Override
    public JSONObject read(InputStream inputStream, StreamWriter streamWriter) {
        JSONObject object = new JSONObject();
        try {
            XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream);
            JSONObject size = new JSONObject();
            size.put("width", xmlSlideShow.getPageSize().width);
            size.put("height", xmlSlideShow.getPageSize().height);
            object.put("size", size);

            JSONArray slides = new JSONArray();
            slides(slides, xmlSlideShow.getSlides(), streamWriter);
            object.put("slides", slides);
            xmlSlideShow.close();
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "解析PPTx数据时发生异常！");
        }

        return object;
    }

    private void slides(JSONArray slides, List<XSLFSlide> xslfSlides, StreamWriter streamWriter) {
        JSONObject slide = new JSONObject();
        xslfSlides.forEach(xslfSlide -> {
            JSONArray elements = new JSONArray();
            xslfSlide.getShapes().forEach(xslfShape -> {
                JSONObject element = new JSONObject();
                getAnchor(element, xslfShape);
                if (xslfShape instanceof XSLFSimpleShape)
                    getRotation(element, (XSLFSimpleShape) xslfShape);
                if (xslfShape instanceof XSLFTextBox)
                    parserHelper.get(Parser.TYPE_TEXT).parse(element, xslfShape, streamWriter);
                else if (xslfShape instanceof XSLFPictureShape)
                    parserHelper.get(Parser.TYPE_IMAGE).parse(element, xslfShape, streamWriter);
                elements.add(element);
            });
            slide.put("elements", elements);
        });
        slides.add(slide);
    }

    private void getAnchor(JSONObject object, XSLFShape xslfShape) {
        Rectangle2D rectangle2D = xslfShape.getAnchor();
        object.put("x", rectangle2D.getX());
        object.put("y", rectangle2D.getY());
        object.put("width", rectangle2D.getWidth());
        object.put("height", rectangle2D.getHeight());
    }

    private void getRotation(JSONObject object, XSLFSimpleShape xslfSimpleShape) {
        if (xslfSimpleShape.getRotation() != 0.0D)
            object.put("rotation", xslfSimpleShape.getRotation());
        if (xslfSimpleShape.getFlipVertical())
            object.put("rotationX", true);
        if (xslfSimpleShape.getFlipHorizontal())
            object.put("rotationY", true);
    }
}
