package org.lwd.frame.poi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lwd.frame.poi.pptx.Parser;
import org.lwd.frame.poi.pptx.ParserHelper;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
@Component("frame.poi.pptx")
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
}
