package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.util.Image;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Numeric;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
@Component("frame.poi.pptx.parser-helper")
public class ParserHelperImpl implements ParserHelper, ContextRefreshedListener {
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    @Inject
    private Image image;
    private Map<String, Parser> parsers;

    @Override
    public Parser get(String type) {
        return parsers.get(type);
    }

    @Override
    public Rectangle getRectangle(JSONObject object) {
        return new Rectangle(object.getIntValue("x"), object.getIntValue("y"),
                object.getIntValue("width"), object.getIntValue("height"));
    }

    @Override
    public void rotate(XSLFSimpleShape xslfSimpleShape, JSONObject object) {
        if (object.containsKey("rotation"))
            xslfSimpleShape.setRotation(object.getDoubleValue("rotation"));
        if (json.hasTrue(object, "rotationX"))
            xslfSimpleShape.setFlipVertical(true);
        if (json.hasTrue(object, "rotationY"))
            xslfSimpleShape.setFlipHorizontal(true);
    }

    @Override
    public Color getColor(JSONObject object, String key) {
        if (!object.containsKey(key))
            return null;

        String color = object.getString(key);
        int[] ns;
        if (color.charAt(0) == '#') {
            String[] array = new String[3];
            boolean full = color.length() == 7;
            for (int i = 0; i < array.length; i++)
                array[i] = full ? color.substring(2 * i + 1, 2 * i + 3) : (color.substring(i + 1, i + 2) + color.substring(i + 1, i + 2));
            ns = new int[3];
            for (int i = 0; i < ns.length; i++)
                ns[i] = Integer.parseInt(array[i], 16);
        } else if (color.indexOf('(') > -1)
            ns = numeric.toInts(color.substring(color.indexOf('(') + 1, color.indexOf(')')));
        else
            ns = numeric.toInts(color);

        return new Color(ns[0], ns[1], ns[2]);
    }

    @Override
    public byte[] getImage(JSONObject object, String contentType, ByteArrayOutputStream outputStream) throws IOException {
        BufferedImage image = this.image.read(outputStream.toByteArray());
        if (image == null)
            return null;

        if (object.containsKey("thumbnail")) {
            JSONObject thumbnail = object.getJSONObject("thumbnail");
            image = this.image.thumbnail(image, thumbnail.getIntValue("width"), thumbnail.getIntValue("height"));
        }
        if (image == null)
            return null;

        if (object.containsKey("subimage")) {
            JSONObject subimage = object.getJSONObject("subimage");
            if (json.hasTrue(object, "rotationX"))
                subimage.put("y", image.getHeight() - subimage.getIntValue("y") - subimage.getIntValue("height"));
            if (json.hasTrue(object, "rotationY"))
                subimage.put("x", image.getWidth() - subimage.getIntValue("x") - subimage.getIntValue("width"));
            image = this.image.subimage(image, subimage.getIntValue("x"), subimage.getIntValue("y"),
                    subimage.getIntValue("width"), subimage.getIntValue("height"));
        }
        if (image == null)
            return null;

        return this.image.write(image, this.image.formatFromContentType(contentType));
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        parsers = new HashMap<>();
        BeanFactory.getBeans(Parser.class).forEach(parser -> parsers.put(parser.getType(), parser));
    }
}