package org.lwd.frame.ctrl;

import org.lwd.frame.atomic.Closables;
import org.lwd.frame.atomic.Failable;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.ctrl.console.Console;
import org.lwd.frame.ctrl.context.Header;
import org.lwd.frame.ctrl.context.Request;
import org.lwd.frame.ctrl.context.Response;
import org.lwd.frame.ctrl.execute.ExecuteInvocation;
import org.lwd.frame.ctrl.execute.ExecutorHelper;
import org.lwd.frame.ctrl.security.Xss;
import org.lwd.frame.ctrl.status.Status;
import org.lwd.frame.ctrl.validate.Validators;
import org.lwd.frame.util.Converter;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.*;

/**
 * @author lwd
 */
@Controller("frame.ctrl.dispatcher")
public class DispatcherImpl implements Dispatcher, Forward, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private Set<Failable> failables;
    @Inject
    private Closables closables;
    @Inject
    private Header header;
    @Inject
    private Request request;
    @Inject
    private Response response;
    @Inject
    private Xss xss;
    @Inject
    private Counter counter;
    @Inject
    private Status status;
    @Inject
    private Console console;
    @Inject
    private Validators validators;
    @Inject
    private Optional<List<Interceptor>> interceptorsOptional;
    @Inject
    private ExecutorHelper executorHelper;
    @Inject
    private Optional<Permit> permit;
    private ThreadLocal<Long> time = new ThreadLocal<>();
    private ThreadLocal<Map<String, Object>> parameters = new ThreadLocal<>();
    private List<Interceptor> interceptors;

    @Override
    public void execute() {
        time.set(System.currentTimeMillis());
        String uri = request.getUri();
        if (logger.isDebugEnable())
            logger.debug("开始处理请求[{}:{}:{}]。", uri, converter.toString(request.getMap()), converter.toString(header.getMap()));

        boolean statusService = status.isStatus(uri);
        String ip = header.getIp();
        if (!counter.increase(uri, ip) && !statusService) {
            failure(uri, ip, Failure.Busy);

            return;
        }

        boolean consoleService = console.isConsole(uri);
        if (!statusService && !consoleService)
            executorHelper.set(uri);
        if (!statusService && !consoleService && executorHelper.get() == null) {
            counter.decrease(uri, ip);
            logger.warn(null, "无法获得请求[{}]的处理服务！", uri);
            response.sendError(404);

            return;
        }

        if (xss.contains(uri, request.getMap())) {
            failure(uri, ip, Failure.Danger);

            return;
        }

        execute(statusService, consoleService);
        closables.close();
        counter.decrease(uri, ip);

        if (logger.isDebugEnable())
            logger.debug("处理请求[{}]完成，耗时[{}]毫秒。", uri, getTime());
    }

    private void failure(String uri, String ip, Failure failure) {
        counter.decrease(uri, ip);
        response.write(failure);
    }

    private void execute(boolean statusService, boolean consoleService) {
        Object object;
        if (statusService)
            object = status.execute(counter.get());
        else if (consoleService)
            object = console.execute();
        else
            object = exe();
        response.write(object);
    }

    @Override
    public Object redirect(String uri) {
        if (logger.isDebugEnable())
            logger.debug("跳转到：{}。", uri);

        executorHelper.set(uri);

        return exe();
    }

    private Object exe() {
        try {
            if (permit.isPresent() && !permit.get().allow())
                return Failure.NotPermit;

            return new ExecuteInvocation(interceptors, validators, executorHelper.get()).invoke();
        } catch (Throwable e) {
            failables.forEach(failable -> failable.fail(e));
            logger.warn(e, "执行请求[{}:{}]时发生异常！", request.getUri(), executorHelper.get().getMethod());

            return Failure.Exception;
        }
    }

    @Override
    public long getTime() {
        return System.currentTimeMillis() - time.get();
    }

    @Override
    public Object redirect(String uri, Map<String, Object> parameters) {
        this.parameters.set(parameters);

        return redirect(uri);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T getParameter(String name) {
        Map<String, Object> parameters = getParameters();
        if (validator.isEmpty(parameters))
            return null;

        return (T) parameters.get(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters.get();
    }

    @Override
    public void redirectTo(String url) {
        response.redirectTo(url);
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        interceptors = new ArrayList<>();
        interceptorsOptional.ifPresent(list -> {
            interceptors.addAll(list);
            interceptors.sort(Comparator.comparingInt(Interceptor::getSort));
        });
    }
}
