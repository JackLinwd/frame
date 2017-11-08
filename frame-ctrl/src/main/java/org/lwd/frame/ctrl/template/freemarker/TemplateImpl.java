package org.lwd.frame.ctrl.template.freemarker;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.Failure;
import org.lwd.frame.ctrl.template.Template;
import org.lwd.frame.ctrl.template.TemplateSupport;
import org.lwd.frame.ctrl.template.Templates;
import org.lwd.frame.freemarker.Freemarker;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
@Controller("frame.ctrl.template.freemarker")
public class TemplateImpl extends TemplateSupport implements Template {
    @Inject
    private Freemarker freemarker;

    @Override
    public String getType() {
        return Templates.FREEMARKER;
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public void process(String name, Object data, OutputStream output) throws IOException {
        if (data instanceof Failure) {
            failure(getFailure((Failure) data), output);

            return;
        }

        if (data instanceof JSONObject && failure((JSONObject) data, output))
            return;

        freemarker.process(name, data, output);
    }

    private boolean failure(JSONObject object, OutputStream outputStream) throws IOException {
        if (object.containsKey("code") && object.getIntValue("code") > 0) {
            outputStream.write(object.toString().getBytes());

            return true;
        }

        return false;
    }
}
