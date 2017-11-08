package org.lwd.frame.script;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.ctrl.validate.ValidateWrapper;
import org.lwd.frame.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Controller(ScriptValidator.NAME)
public class ScriptValidatorImpl extends ValidatorSupport {
    @Inject
    private ScriptService scriptService;
    private ThreadLocal<JSONObject> json = new ThreadLocal<>();

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        json.remove();
        json.set(scriptService.validate(validate.getString(), parameter));

        return getCode() == 0;
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return getCode();
    }

    @Override
    public String getFailureMessage(ValidateWrapper validate) {
        int failureCode = getFailureCode(validate);
        if (failureCode == 9999)
            return message.get("frame.ctrl.exception");

        JSONObject json = this.json.get();
        if (failureCode == 9996)
            return message.get("frame.script.validate.name.not-exists", json.getString("name"));

        return json.getString("failure");
    }

    private int getCode() {
        JSONObject json = this.json.get();

        return json == null ? 0 : json.getIntValue("code");
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return null;
    }
}
