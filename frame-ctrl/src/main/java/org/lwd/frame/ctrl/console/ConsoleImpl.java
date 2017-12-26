package org.lwd.frame.ctrl.console;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.crypto.Sign;
import org.lwd.frame.ctrl.context.Header;
import org.lwd.frame.ctrl.context.Request;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Json;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Numeric;
import org.lwd.frame.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lwd
 */
@Service("frame.ctrl.console")
public class ConsoleImpl implements Console, ContextRefreshedListener {
    @Inject
    private Sign sign;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private Header header;
    @Inject
    private Request request;
    @Value("${frame.ctrl.console.uri:/frame/ctrl/console}")
    private String uri;
    @Value("${frame.ctrl.console.allow-ips:}")
    private String allowIps;
    private boolean enable;
    private Set<String> allowIpSet;

    @Override
    public boolean isConsole(String uri) {
        return enable && this.uri.equals(uri);
    }

    @Override
    public JSONObject execute() {
        if (!isAllowIp())
            return json(9901, null);

        if (!sign.verify(request.getMap(), "frame-ctrl-console"))
            return json(9995, null);

        String beanName = request.get("beanName");
        if (validator.isEmpty(beanName))
            return json(9902, null);

        String fieldName = request.get("fieldName");
        String methodName = request.get("methodName");
        if (validator.isEmpty(methodName) && validator.isEmpty(fieldName))
            return json(9903, null);

        Object bean = BeanFactory.getBean(beanName);
        if (bean == null)
            return json(9904, null);

        List<Class<?>> classes = new ArrayList<>();
        List<Object> args = new ArrayList<>();
        parseArgs(classes, args);

        try {
            return validator.isEmpty(methodName) ? field(bean, fieldName, args) : method(bean, methodName, classes, args);
        } catch (Exception e) {
            logger.warn(e, "执行控制器时发生异常！");

            return json(9999, null);
        }
    }

    private boolean isAllowIp() {
        if (allowIpSet == null)
            allowIpSet = converter.toSet(converter.toArray(allowIps, ","));

        return allowIpSet.contains("*") || allowIpSet.contains(header.getIp());
    }

    private void parseArgs(List<Class<?>> classes, List<Object> args) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String arg = request.get("arg" + i);
            if (validator.isEmpty(arg))
                break;

            int indexOf = arg.indexOf(':');
            if (indexOf == -1)
                continue;

            String type = arg.substring(0, indexOf);
            String value = arg.substring(indexOf + 1);
            if (value.equals("null"))
                value = null;
            if (type.equals("string")) {
                classes.add(String.class);
                args.add(value);

                continue;
            }

            if (type.equals("int")) {
                classes.add(int.class);
                args.add(numeric.toInt(value));

                continue;
            }

            if (type.equals("long")) {
                classes.add(long.class);
                args.add(numeric.toLong(value));

                continue;
            }

            if (type.equals("boolean")) {
                classes.add(boolean.class);
                args.add(Boolean.parseBoolean(value));

                continue;
            }

            if (type.equals("json")) {
                classes.add(JSONObject.class);
                args.add(json.toObject(value));
            }
        }
    }

    private JSONObject field(Object bean, String fieldName, List<Object> args) throws Exception {
        Field field = bean.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        if (args.isEmpty())
            return json(0, field.get(bean));

        field.set(bean, args.get(0));

        return json(0, null);
    }

    private JSONObject method(Object bean, String methodName, List<Class<?>> classes, List<Object> args) throws Exception {
        Method method = bean.getClass().getDeclaredMethod(methodName, classes.toArray(new Class<?>[0]));
        method.setAccessible(true);

        return json(0, method.invoke(bean, args.toArray()));
    }

    private JSONObject json(int code, Object result) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        if (result != null)
            json.put("result", result);

        return json;
    }

    @Override
    public int getContextRefreshedSort() {
        return 7;
    }

    @Override
    public void onContextRefreshed() {
        enable = !validator.isEmpty(uri);
        if (logger.isInfoEnable())
            logger.info("设置控制台启动状态：{}", enable);
    }
}