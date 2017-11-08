package org.lwd.frame.script;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Service("frame.script.service")
public class ScriptServiceImpl implements ScriptService {
    private static final String METHOD = "frame.validate";

    @Inject
    private Engine engine;
    @Inject
    private Arguments arguments;

    @Override
    public JSONObject validate(String[] names, String parameter) {
        arguments.set("parameter", parameter);
        arguments.set("names", names);

        return JSON.parseObject(engine.execute(METHOD).toString());
    }
}
