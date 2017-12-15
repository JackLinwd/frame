package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Numeric;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeStyle;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

/**
 * @author lwd
 */
@Component("frame.poi.pptx.text")
public class TextParserImpl implements Parser {
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        XSLFTextBox xslfTextBox = xslfSlide.createTextBox();
        xslfTextBox.clearText();
        xslfTextBox.setAnchor(parserHelper.getRectangle(object));
        xslfTextBox.setInsets(new Insets2D(0.0D, 0.0D, 0.0D, 0.0D));
        parserHelper.rotate(xslfTextBox, object);
        XSLFTextParagraph xslfTextParagraph = newParagraph(xslfTextBox, object);
        if (object.containsKey("texts")) {
            JSONArray texts = object.getJSONArray("texts");
            for (int i = 0, size = texts.size(); i < size; i++)
                xslfTextParagraph = add(xslfTextBox, xslfTextParagraph, object, texts.getJSONObject(i));
        } else if (object.containsKey("text"))
            add(xslfTextBox, xslfTextParagraph, object, new JSONObject());

        return true;
    }

    private XSLFTextParagraph add(XSLFTextBox xslfTextBox, XSLFTextParagraph xslfTextParagraph, JSONObject object, JSONObject child) {
        String text = child.containsKey("text") ? child.getString("text") : object.getString("text");
        boolean empty = text.equals("\n");
        if (empty)
            xslfTextParagraph = newParagraph(xslfTextBox, object);
        XSLFTextRun xslfTextRun = xslfTextParagraph.addNewTextRun();
        font(xslfTextParagraph, xslfTextRun, object, child);
        color(xslfTextRun, object, child);
        if (hasTrue(object, child, "bold"))
            xslfTextRun.setBold(true);
        if (hasTrue(object, child, "underline"))
            xslfTextRun.setUnderlined(true);
        if (hasTrue(object, child, "italic"))
            xslfTextRun.setItalic(true);
        if (hasTrue(object, child, "strikethrough"))
            xslfTextRun.setStrikethrough(true);
        if (object.containsKey("spacing") || child.containsKey("spacing"))
            xslfTextRun.setCharacterSpacing((child.containsKey("spacing") ? child : object).getDoubleValue("spacing"));
        xslfTextRun.setText(empty ? "" : text);

        return xslfTextParagraph;
    }

    private XSLFTextParagraph newParagraph(XSLFTextBox xslfTextBox, JSONObject object) {
        XSLFTextParagraph xslfTextParagraph = xslfTextBox.addNewTextParagraph();
        align(xslfTextParagraph, object);

        return xslfTextParagraph;
    }

    private void align(XSLFTextParagraph xslfTextParagraph, JSONObject object) {
        if (!object.containsKey("align"))
            return;

        switch (object.getString("align")) {
            case "left":
                xslfTextParagraph.setTextAlign(TextParagraph.TextAlign.LEFT);
                break;
            case "center":
                xslfTextParagraph.setTextAlign(TextParagraph.TextAlign.CENTER);
                break;
            case "right":
                xslfTextParagraph.setTextAlign(TextParagraph.TextAlign.RIGHT);
                break;
            default:
                xslfTextParagraph.setTextAlign(TextParagraph.TextAlign.JUSTIFY);
        }
    }

    private void font(XSLFTextParagraph xslfTextParagraph, XSLFTextRun xslfTextRun, JSONObject object, JSONObject child) {
        if (!object.containsKey("font") && !child.containsKey("font"))
            return;

        JSONObject font = (child.containsKey("font") ? child : object).getJSONObject("font");
        if (font.containsKey("family"))
            xslfTextRun.setFontFamily(font.getString("family"), FontGroup.LATIN);
        if (font.containsKey("size"))
            xslfTextRun.setFontSize(font.getDoubleValue("size"));
        if (font.containsKey("height"))
            xslfTextParagraph.setLineSpacing(font.getDoubleValue("height") * 75);
    }

    private void color(XSLFTextRun xslfTextRun, JSONObject object, JSONObject child) {
        if (!object.containsKey("color") && !child.containsKey("color"))
            return;

        Color color = parserHelper.getColor(child.containsKey("color") ? child : object, "color");
        if (color != null)
            xslfTextRun.setFontColor(color);
    }

    private boolean hasTrue(JSONObject object, JSONObject child, String key) {
        return json.hasTrue(object, key) || json.hasTrue(child, key);
    }
}