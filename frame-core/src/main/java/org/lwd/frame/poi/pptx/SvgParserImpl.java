package org.lwd.frame.poi.pptx;

import com.alibaba.fastjson.JSONObject;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
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
import java.io.Reader;
import java.io.StringReader;

/**
 * @author lwd
 */
@Component("frame.poi.pptx.svg")
public class SvgParserImpl implements Parser {
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Inject
    private ParserHelper parserHelper;

    @Override
    public String getType() {
        return "svg";
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
        } catch (IOException | TranscoderException e) {
            logger.warn(e, "解析SVG图片[{}]时发生异常！", object.toJSONString());

            return false;
        }
    }

    private ByteArrayOutputStream readSvg(String image) throws IOException, TranscoderException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Reader reader = new StringReader(image);
        new PNGTranscoder().transcode(new TranscoderInput(reader), new TranscoderOutput(outputStream));
        reader.close();
        outputStream.flush();
        outputStream.close();

        return outputStream;
    }
}
