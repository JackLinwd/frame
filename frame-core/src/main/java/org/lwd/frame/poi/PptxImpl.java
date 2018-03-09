package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.FillStyle;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.xslf.usermodel.*;
import org.lwd.frame.poi.pptx.Parser;
import org.lwd.frame.poi.pptx.ParserHelper;
import org.lwd.frame.util.*;
import org.lwd.frame.util.Image;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lwd
 */
@Component("frame.poi.pptx")
public class PptxImpl implements Pptx {
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private DateTime dateTime;
    @Inject
    private Image image;
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

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -1 * (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)));
        String time = dateTime.toString(calendar.getTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        xmlSlideShow.getProperties().getCoreProperties().setCreated(time);
        xmlSlideShow.getProperties().getCoreProperties().setModified(time);

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
            size.put("x", 0);
            size.put("y", 0);
            size.put("width", xmlSlideShow.getPageSize().width);
            size.put("height", xmlSlideShow.getPageSize().height);
            object.put("size", size);

            JSONArray slides = new JSONArray();
            slides(slides, xmlSlideShow.getSlides(), size, layouts(xmlSlideShow, size, streamWriter), streamWriter);
            object.put("slides", slides);
            xmlSlideShow.close();
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "解析PPTx数据时发生异常！");
        }

        return object;
    }

    private Map<String, JSONArray> layouts(XMLSlideShow xmlSlideShow, JSONObject size, StreamWriter streamWriter) {
        Map<String, JSONArray> map = new HashMap<>();
        xmlSlideShow.getSlideMasters().forEach(xslfSlideMaster -> {
            for (XSLFSlideLayout xslfSlideLayout : xslfSlideMaster.getSlideLayouts()) {
                JSONArray elements = new JSONArray();
                fillStyle(elements, size, xslfSlideLayout.getBackground().getFillStyle(), true, streamWriter);
                shapes(elements, xslfSlideLayout.getShapes(), streamWriter);
                map.put(xslfSlideLayout.getName(), elements);
            }
        });

        return map;
    }

    private void slides(JSONArray slides, List<XSLFSlide> xslfSlides, JSONObject size, Map<String, JSONArray> layouts,
                        StreamWriter streamWriter) {
        xslfSlides.forEach(xslfSlide -> {
            JSONArray elements = new JSONArray();
            if (layouts.containsKey(xslfSlide.getSlideLayout().getName()))
                elements.addAll(layouts.get(xslfSlide.getSlideLayout().getName()));
            fillStyle(elements, size, xslfSlide.getBackground().getFillStyle(), true, streamWriter);
            shapes(elements, xslfSlide.getShapes(), streamWriter);
            JSONObject slide = new JSONObject();
            slide.put("elements", elements);
            slides.add(slide);
        });
    }

    private void shapes(JSONArray elements, List<XSLFShape> shapes, StreamWriter streamWriter) {
        shapes.forEach(xslfShape -> {
            JSONObject element = new JSONObject();
            getAnchor(element, xslfShape);
            if (xslfShape instanceof XSLFSimpleShape) {
                XSLFSimpleShape xslfSimpleShape = (XSLFSimpleShape) xslfShape;
                getRotation(element, xslfSimpleShape);
                if (xslfSimpleShape.getShapeType() != null)
                    element.put("shape", xslfSimpleShape.getShapeType().getOoxmlName());
                fillStyle(elements, element, xslfSimpleShape.getFillStyle(), false, streamWriter);
                getBorder(elements, element, xslfSimpleShape);
            }
            if (xslfShape instanceof XSLFTextShape)
                parserHelper.get(Parser.TYPE_TEXT).parse(element, xslfShape, streamWriter);
            else if (xslfShape instanceof XSLFPictureShape)
                parserHelper.get(Parser.TYPE_IMAGE).parse(element, xslfShape, streamWriter);
            else if (xslfShape instanceof XSLFGroupShape)
                shapes(elements, ((XSLFGroupShape) xslfShape).getShapes(), streamWriter);
            else
                screenshot(element, xslfShape, streamWriter);
            elements.add(element);
        });
    }

    private void getAnchor(JSONObject object, XSLFShape xslfShape) {
        Rectangle2D rectangle2D = xslfShape.getAnchor();
        object.put("x", numeric.toInt(rectangle2D.getX()));
        object.put("y", numeric.toInt(rectangle2D.getY()));
        object.put("width", numeric.toInt(rectangle2D.getWidth()));
        object.put("height", numeric.toInt(rectangle2D.getHeight()));
    }

    private void getRotation(JSONObject object, XSLFSimpleShape xslfSimpleShape) {
        if (xslfSimpleShape.getRotation() != 0.0D)
            object.put("rotation", numeric.toInt(xslfSimpleShape.getRotation()));
        if (xslfSimpleShape.getFlipVertical())
            object.put("rotationX", true);
        if (xslfSimpleShape.getFlipHorizontal())
            object.put("rotationY", true);
    }

    private void fillStyle(JSONArray elements, JSONObject element, FillStyle fillStyle, boolean ignoreWhite, StreamWriter streamWriter) {
        if (fillStyle == null)
            return;

        PaintStyle paintStyle = fillStyle.getPaint();
        if (paintStyle == null)
            return;

        if (paintStyle instanceof PaintStyle.SolidPaint)
            fillColor(elements, element, paintStyle, ignoreWhite);
        else if (paintStyle instanceof PaintStyle.TexturePaint)
            fillTexture(elements, element, paintStyle, streamWriter);
    }

    private void fillColor(JSONArray elements, JSONObject element, PaintStyle paintStyle, boolean ignoreWhite) {
        String color = parserHelper.getHexColor(paintStyle, ignoreWhite);
        if (color == null)
            return;

        JSONObject object = new JSONObject();
        object.putAll(element);
        object.put("type", "bgcolor");
        object.put("bgcolor", color);
        elements.add(object);
    }

    private void fillTexture(JSONArray elements, JSONObject element, PaintStyle paintStyle, StreamWriter streamWriter) {
        PaintStyle.TexturePaint texturePaint = (PaintStyle.TexturePaint) paintStyle;
        try {
            InputStream inputStream = texturePaint.getImageData();
            JSONObject object = new JSONObject();
            object.putAll(element);
            object.put(Parser.TYPE_IMAGE, streamWriter.write(texturePaint.getContentType(), "", inputStream));
            elements.add(object);
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "保存图片[{}]流数据时发生异常！", texturePaint.getContentType());
        }
    }

    private void getBorder(JSONArray elements, JSONObject element, XSLFSimpleShape xslfSimpleShape) {
        if (xslfSimpleShape.getLineDash() == null)
            return;

        int width = numeric.toInt(xslfSimpleShape.getLineWidth() * 96 / 72);
        if (width <= 0)
            return;

        JSONObject object = new JSONObject();
        object.putAll(element);
        object.put("type", "border");
        object.put("color", parserHelper.toHex(xslfSimpleShape.getLineColor()));
        object.put("dash", xslfSimpleShape.getLineDash().name().toLowerCase());
        object.put("border", width);
        elements.add(object);
    }

    private void screenshot(JSONObject object, XSLFShape xslfShape, StreamWriter streamWriter) {
        System.out.println("pptx:" + xslfShape);
//        if (logger.isInfoEnable())
//            logger.info("无法解析PPTx元素[{}]。", xslfShape);
//
//        int width = object.getIntValue("width");
//        int height = object.getIntValue("height");
//        if (width <= 0 || height <= 0)
//            return;
//
//        try {
//            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
//            Graphics2D graphics2D = image.createGraphics();
//            xslfShape.draw(graphics2D, new Rectangle2D.Double(0, 0, width, height));
//            graphics2D.dispose();
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            ImageIO.write(image, "PNG", outputStream);
//            outputStream.close();
//
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
//            object.put(Parser.TYPE_IMAGE, streamWriter.write("image/png", "", inputStream));
//            inputStream.close();
//        } catch (Exception e) {
//            logger.warn(e, "截取PPTx形状为图片时发生异常！");
//        }
    }
}
