package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.common.usermodel.fonts.FontGroup;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;
import org.lwd.frame.poi.StreamWriter;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Numeric;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;

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
    private Validator validator;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public String getType() {
        return TYPE_TEXT;
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        XSLFTextBox xslfTextBox = xslfSlide.createTextBox();
        xslfTextBox.clearText();
        xslfTextBox.setInsets(new Insets2D(0.0D, 0.0D, 0.0D, 0.0D));
        xslfTextBox.setAnchor(parserHelper.getRectangle(object));
        parserHelper.rotate(xslfTextBox, object);
        XSLFTextParagraph xslfTextParagraph = newParagraph(xslfTextBox, object);
        if (object.containsKey("texts")) {
            JSONArray texts = object.getJSONArray("texts");
            for (int i = 0, size = texts.size(); i < size; i++)
                xslfTextParagraph = add(xslfTextBox, xslfTextParagraph, object, texts.getJSONObject(i));
        } else if (object.containsKey(getType()))
            add(xslfTextBox, xslfTextParagraph, object, new JSONObject());

        return true;
    }

    private XSLFTextParagraph add(XSLFTextBox xslfTextBox, XSLFTextParagraph xslfTextParagraph, JSONObject object, JSONObject child) {
        String text = child.containsKey(getType()) ? child.getString(getType()) : object.getString(getType());
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

    @Override
    public boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter writer) {
        object.put("type", getType());
        JSONArray texts = new JSONArray();
        XSLFTextShape xslfTextShape = (XSLFTextShape) xslfShape;
        object.put("verticalAlign", getVerticalAlign(xslfTextShape));
        xslfTextShape.getTextParagraphs().forEach(xslfTextParagraph -> {
            JSONObject paragraph = new JSONObject();
            paragraph.put("type", getType());
            paragraph.put("align", getTextAlign(xslfTextParagraph));

            if (!texts.isEmpty()) {
                JSONObject text = new JSONObject();
                text.putAll(paragraph);
                text.put(getType(), "\n");
                texts.add(text);
            }

            String fontFamily = xslfTextParagraph.getDefaultFontFamily();
            Double fontSize = xslfTextParagraph.getDefaultFontSize();
            xslfTextParagraph.getTextRuns().forEach(xslfTextRun -> {
                JSONObject text = new JSONObject();
                text.putAll(paragraph);
                text.put(getType(), xslfTextRun.getRawText());
                text.put("spacing", xslfTextRun.getCharacterSpacing());
                if (xslfTextRun.isBold())
                    text.put("bold", true);
                if (xslfTextRun.isItalic())
                    text.put("italic", true);
                if (xslfTextRun.isUnderlined())
                    text.put("underline", true);
                if (xslfTextRun.isStrikethrough())
                    text.put("strikethrough", true);
                text.put("font", getFont(fontFamily, fontSize, xslfTextRun));
                text.put("color", parserHelper.getHexColor(xslfTextRun.getFontColor(), false));
                texts.add(text);
            });
        });
        if (texts.isEmpty())
            return false;

        if (texts.size() == 1)
            object.putAll(texts.getJSONObject(0));
        else
            object.put("texts", texts);

        return true;
    }

    private String getVerticalAlign(XSLFTextShape xslfTextShape) {
        switch (xslfTextShape.getVerticalAlignment()) {
            case MIDDLE:
                return "middle";
            case BOTTOM:
                return "bottom";
            default:
                return null;
        }
    }

    private String getTextAlign(XSLFTextParagraph xslfTextParagraph) {
        switch (xslfTextParagraph.getTextAlign()) {
            case LEFT:
                return "left";
            case CENTER:
                return "center";
            case RIGHT:
                return "right";
            default:
                return "justify";
        }
    }

    private JSONObject getFont(String fontFamily, Double fontSize, XSLFTextRun xslfTextRun) {
        JSONObject font = new JSONObject();
        font.put("family", validator.isEmpty(xslfTextRun.getFontFamily()) ? fontFamily : xslfTextRun.getFontFamily());
        Double size = xslfTextRun.getFontSize() == null ? fontSize : xslfTextRun.getFontSize();
        if (size != null)
            font.put("size", numeric.toInt(size * 96 / 72));

        return font;
    }
}