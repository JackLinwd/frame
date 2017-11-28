package org.lwd.frame.ctrl.execute;

import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.ctrl.FailureCode;
import org.lwd.frame.ctrl.context.Request;
import org.lwd.frame.ctrl.template.Templates;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Numeric;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author lwd
 */
@Controller("frame.ctrl.execute.map")
public class ExecutorHelperImpl implements ExecutorHelper, FailureCode, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private Templates templates;
    @Inject
    private Request request;
    @Inject
    private Optional<Set<ExecuteListener>> listeners;
    private Map<String, Executor> map;
    private Map<String, String> codes;
    private ThreadLocal<Executor> executors = new ThreadLocal<>();

    @Override
    public void set(String service) {
        executors.remove();
        if (setByKey(service))
            return;

        for (String regex : map.keySet())
            if (validator.isMatchRegex(regex, service) && setByKey(regex))
                return;
    }

    private boolean setByKey(String key) {
        Executor executor = map.get(key);
        if (executor != null) {
            executors.set(executor);

            return true;
        }

        return false;
    }

    @Override
    public Executor get() {
        return executors.get();
    }

    @Override
    public int get(int code) {
        return get(request.getUri(), code);
    }

    @Override
    public int get(String uri, int code) {
        String prefix = codes.get(uri);
        if (prefix != null)
            return getCode(prefix, code);

        for (String regex : codes.keySet())
            if (validator.isMatchRegex(regex, uri))
                return getCode(codes.get(regex), code);

        return code;
    }

    private int getCode(String prefix, int code) {
        int n = numeric.toInt(prefix + numeric.toString(code, "00"));

        return n == 0 ? -1 : n;
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        if (map != null)
            return;

        map = new HashMap<>();
        codes = new HashMap<>();
        for (String name : BeanFactory.getBeanNames()) {
            Class<?> clazz = BeanFactory.getBeanClass(name);
            Execute classExecute = clazz.getAnnotation(Execute.class);
            String prefix = classExecute == null ? "" : classExecute.name();
            String prefixCode = classExecute == null ? "" : classExecute.code();
            for (Method method : clazz.getMethods()) {
                Execute execute = method.getAnnotation(Execute.class);
                if (execute == null || validator.isEmpty(prefix + execute.name()))
                    continue;

                Executor executor = new ExecutorImpl(BeanFactory.getBean(name), method, getKey(classExecute, execute),
                        execute.validates(), templates.get(execute.type()), prefix + execute.template());
                String code = prefixCode + execute.code();
                for (String service : converter.toArray(execute.name(), ",")) {
                    String key = prefix + service;
                    map.put(key, executor);
                    codes.put(key, code);
                }
                listeners.ifPresent(set -> set.forEach(listener -> listener.definition(classExecute, execute, executor)));
            }
        }

        if (logger.isInfoEnable()) {
            StringBuilder sb = new StringBuilder().append("共[").append(map.size()).append("]个服务[");
            boolean hasElement = false;
            for (String key : map.keySet()) {
                if (hasElement)
                    sb.append(',');
                sb.append(key);
                hasElement = true;
            }
            logger.info(sb.append("]。").toString());
        }
    }

    private String getKey(Execute classExecute, Execute execute) {
        if (!validator.isEmpty(execute.key()))
            return execute.key();

        return classExecute == null ? "" : classExecute.key();
    }
}
