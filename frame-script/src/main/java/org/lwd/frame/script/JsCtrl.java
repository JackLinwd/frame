package org.lwd.frame.script;

import org.lwd.frame.ctrl.context.Request;
import org.lwd.frame.ctrl.context.Response;
import org.lwd.frame.ctrl.execute.Execute;
import org.lwd.frame.ctrl.template.Templates;
import org.lwd.frame.ctrl.validate.Validate;
import org.lwd.frame.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lwd
 */
@Controller("frame.script.js.ctrl")
@Execute(name = "/frame/script/", key = "frame.script", code = "99")
public class JsCtrl {
    private static final String METHOD = "method";

    @Inject
    private Request request;
    @Inject
    private Response response;
    @Inject
    private Engine engine;

    @Execute(name = "js", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = METHOD, failureCode = 1),
            @Validate(validator = ScriptService.VALIDATOR_EXISTS_METHOD, parameter = METHOD, failureCode = 2)})
    public Object execute() {
        return engine.execute(request.get(METHOD));
    }

    @Execute(name = "debug", type = Templates.FREEMARKER, template = "debug")
    public Object debug() {
        Map<String, Object> map = new HashMap<>();
        map.put("names", engine.names());

        return map;
    }

    @Execute(name = "debug/.+", type = Templates.STRING, validates = {
            @Validate(validator = ScriptValidator.NAME, parameter = "parameter", string = {"validator1", "validator2"})
    })
    public Object debugScript() {
        response.setContentType("text/javascript");
        String uri = request.getUri();

        return engine.read(uri.substring(uri.lastIndexOf('/')));
    }
}
