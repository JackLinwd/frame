package org.lwd.frame.ctrl.template.pptx;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.context.Response;
import org.lwd.frame.ctrl.template.TemplateSupport;
import org.lwd.frame.ctrl.template.Templates;
import org.lwd.frame.poi.Pptx;
import org.lwd.frame.util.Coder;
import org.lwd.frame.util.Context;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
@Controller("frame.ctrl.template.pptx")
public class TemplateImpl extends TemplateSupport {
    @Inject
    private Context context;
    @Inject
    private Coder coder;
    @Inject
    private Pptx pptx;
    @Inject
    private Response response;

    @Override
    public String getType() {
        return Templates.PPTX;
    }

    @Override
    public String getContentType() {
        return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (!(data instanceof JSONObject))
            return;

        JSONObject object = (JSONObject) data;
        if (object.containsKey("filename"))
            response.setHeader("Content-Disposition", "attachment; filename*=" + context.getCharset(null)
                    + "''" + coder.encodeUrl(object.getString("filename"), null) + ".pptx");
        pptx.write(object, outputStream);
    }
}
