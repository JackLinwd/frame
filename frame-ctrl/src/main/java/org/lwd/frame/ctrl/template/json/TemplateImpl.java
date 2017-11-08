package org.lwd.frame.ctrl.template.json;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.Dispatcher;
import org.lwd.frame.ctrl.Failure;
import org.lwd.frame.ctrl.template.Template;
import org.lwd.frame.ctrl.template.TemplateSupport;
import org.lwd.frame.ctrl.template.Templates;
import org.lwd.frame.dao.model.Model;
import org.lwd.frame.dao.model.ModelHelper;
import org.lwd.frame.dao.orm.PageList;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Message;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lwd
 */
@Controller("frame.ctrl.template.json")
public class TemplateImpl extends TemplateSupport implements Template {
    @Inject
    private Validator validator;
    @Inject
    private Message message;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Dispatcher dispatcher;

    @Override
    public String getType() {
        return Templates.JSON;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public void process(String name, Object data, OutputStream outputStream) throws IOException {
        if (data instanceof Failure) {
            write(getFailure((Failure) data), outputStream);

            return;
        }

        if (data instanceof Model)
            data = modelHelper.toJson((Model) data);
        else if (data instanceof PageList)
            data = ((PageList<? extends Model>) data).toJson();
        else if (data instanceof String)
            data = json((String) data);

        write(pack(data), outputStream);
    }

    private Object json(String string) {
        if (string.length() == 0)
            return string;

        char ch = string.charAt(0);
        if (ch == '{')
            return json.toObject(string);

        if (ch == '[')
            return json.toArray(string);

        return string;
    }

    private Object pack(Object object) {
        if (object instanceof JSONObject) {
            JSONObject json = (JSONObject) object;
            if (json.containsKey("code")) {
                putTime(json);

                return object;
            }
        }

        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("data", object);
        putTime(json);

        return json;
    }

    private void putTime(JSONObject object) {
        object.put("time", dispatcher.getTime());
    }

    private void write(Object data, OutputStream outputStream) throws IOException {
        outputStream.write(json.toBytes(data));
    }
}
