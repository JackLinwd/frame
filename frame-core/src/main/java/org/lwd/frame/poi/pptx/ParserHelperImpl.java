package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Numeric;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
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
    public byte[] subImage(byte[] data, JSONObject object, String format) throws IOException {
        if (!object.containsKey("sub"))
            return data;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.close();

        int width = image.getWidth();
        int height = image.getHeight();
        JSONObject sub = object.getJSONObject("sub");
        int x = sub.getIntValue("x");
        int y = sub.getIntValue("y");
        int w = sub.getIntValue("width");
        int h = sub.getIntValue("height");
        if (x == 0 && y == 0 && w >= width && h >= height)
            return data;

        BufferedImage subImage = image.getSubimage(x, y, Math.min(w, width - x), Math.min(h, height - y));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(subImage, format, outputStream);
        outputStream.close();

        return outputStream.toByteArray();
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
