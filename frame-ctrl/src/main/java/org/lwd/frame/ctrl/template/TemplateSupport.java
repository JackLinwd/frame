package org.lwd.frame.ctrl.template;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.Failure;
import org.lwd.frame.util.Message;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

/**
 * 模板支持类。
 *
 * @author lwd
 */
public abstract class TemplateSupport implements Template {
    @Inject
    protected Validator validator;
    @Inject
    protected Message message;
    @Value("${frame.ctrl.dispatcher.exception:9999}")
    protected int exception;
    @Value("${frame.ctrl.dispatcher.busy:9998}")
    protected int busy;
    @Value("${frame.ctrl.dispatcher.danger:9997}")
    protected int danger;
    @Value("${frame.ctrl.dispatcher.not-permit:9996}")
    protected int notPermit;

    @Override
    public Object failure(int code, String message, String parameter, String value) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("message", message);
        JSONObject object = new JSONObject();
        object.put("name", parameter);
        object.put("value", value);
        json.put("parameter", object);

        return json;
    }

    @Override
    public Object success(Object data, String key, Object... args) {
        JSONObject object = new JSONObject();
        object.put("code", 0);
        if (data != null)
            object.put("data", data);
        if (!validator.isEmpty(key))
            object.put("message", message.get(key, args));

        return object;
    }

    protected JSONObject getFailure(Failure failure) {
        JSONObject object = new JSONObject();
        object.put("code", getFailureCode(failure));
        object.put("message", message.get(failure.getMessageKey()));

        return object;
    }

    protected int getFailureCode(Failure failure) {
        if (failure == Failure.NotPermit)
            return notPermit;

        if (failure == Failure.Danger)
            return danger;

        if (failure == Failure.Busy)
            return busy;

        return exception;
    }
}
