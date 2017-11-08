package org.lwd.frame.ctrl.template.stream;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.Failure;
import org.lwd.frame.ctrl.context.Response;
import org.lwd.frame.ctrl.template.Template;
import org.lwd.frame.ctrl.template.TemplateSupport;
import org.lwd.frame.ctrl.template.Templates;
import org.lwd.frame.util.Context;
import org.lwd.frame.util.Json;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
@Controller("frame.ctrl.template.stream")
public class TemplateImpl extends TemplateSupport implements Template {
    @Inject
    private Context context;
    @Inject
    private Json json;
    @Inject
    private Response response;

    @Override
    public String getType() {
        return Templates.STREAM;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (data instanceof Failure)
            data = getFailure((Failure) data);
        if (data instanceof JSONObject) {
            response.setContentType("application/json");
            data = json.toBytes(data);
        }

        outputStream.write((byte[]) data);
    }
}
