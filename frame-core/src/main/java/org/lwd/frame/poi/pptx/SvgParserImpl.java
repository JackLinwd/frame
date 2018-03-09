package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.*;
import org.lwd.frame.poi.StreamWriter;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Http;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author lwd
 */
@Component("frame.poi.pptx.svg")
public class SvgParserImpl implements Parser {
    @Inject
    private Http http;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private ParserHelper parserHelper;
    private Pattern pattern = Pattern.compile("viewBox=\"[^\"]+\"");

    @Override
    public String getType() {
        return TYPE_SVG;
    }

    @Override
    public boolean parse(XMLSlideShow xmlSlideShow, XSLFSlide xslfSlide, JSONObject object) {
        try {
            XSLFPictureData xslfPictureData = xmlSlideShow.addPicture(parserHelper.getImage(object, "image/png",
                    readSvg(object.getString("svg"))), PictureData.PictureType.PNG);
            XSLFPictureShape xslfPictureShape = xslfSlide.createPicture(xslfPictureData);
            xslfPictureShape.setAnchor(parserHelper.getRectangle(object));
            parserHelper.rotate(xslfPictureShape, object);

            return true;
        } catch (Throwable e) {
            logger.warn(e, "解析SVG图片[{}]时发生异常！", object.toJSONString());

            return false;
        }
    }

    private ByteArrayOutputStream readSvg(String image) throws IOException, TranscoderException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reader reader = new StringReader(fixViewBox(image));
        new PNGTranscoder().transcode(new TranscoderInput(reader), new TranscoderOutput(outputStream));
        reader.close();
        outputStream.flush();
        outputStream.close();

        return outputStream;
    }

    private String fixViewBox(String image) {
        Matcher matcher = pattern.matcher(image);
        if (!matcher.find())
            return image;

        String viewBox = matcher.group();
        String[] array = converter.toArray(viewBox.substring(9, viewBox.length() - 1), " ");
        double[] ns = new double[]{numeric.toDouble(array[2]), numeric.toDouble(array[3])};
        if (ns[0] <= 2048 && ns[1] <= 2048)
            return image;

        if (ns[0] > 2048) {
            ns[1] = ns[1] * 2048 / ns[0];
            ns[0] = 2048;
        }
        if (ns[1] > 2048) {
            ns[0] = ns[0] * 2048 / ns[1];
            ns[1] = 2048;
        }

        return matcher.replaceFirst("viewBox=\"0 0 " + ns[0] + " " + ns[1] + "\"");
    }

    @Override
    public boolean parse(JSONObject object, XSLFShape xslfShape, StreamWriter writer) {
        return false;
    }
}
