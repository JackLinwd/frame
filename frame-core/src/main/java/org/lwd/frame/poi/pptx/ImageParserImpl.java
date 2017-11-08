package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.lwd.frame.util.Http;
import org.lwd.frame.util.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author lwd
 */
@Component("frame.poi.pptx.image")
public class ImageParserImpl implements Parser {
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public String getType() {
        return "image";
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String image = object.getString("image");
        Map<String, String> map = http.download(image, null, null, outputStream);
        if (map == null)
            return false;

        try {
            String contenType = map.get("Content-Type");
            XSLFPictureData xslfPictureData = xmlSlideShow.addPicture(parserHelper.subImage(outputStream.toByteArray(),
                    object, contenType.substring(contenType.indexOf('/') + 1).toUpperCase()), getPictureType(contenType));
            XSLFPictureShape xslfPictureShape = xslfSlide.createPicture(xslfPictureData);
            xslfPictureShape.setAnchor(parserHelper.getRectangle(object));
            parserHelper.rotate(xslfPictureShape, object);

            return true;
        } catch (IOException e) {
            logger.warn(e, "解析图片[{}]时发生异常！", object.toJSONString());

            return false;
        }
    }

    private PictureData.PictureType getPictureType(String contentType) {
        if (contentType.equals("image/jpeg"))
            return PictureData.PictureType.JPEG;

        if (contentType.equals("image/gif"))
            return PictureData.PictureType.GIF;

        if (!contentType.equals("image/png"))
            logger.warn(null, "未处理图片类型[{}]！", contentType);

        return PictureData.PictureType.PNG;
    }
}
