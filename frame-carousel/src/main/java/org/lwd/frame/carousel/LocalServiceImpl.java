package org.lwd.frame.carousel;

import org.lwd.frame.ctrl.Dispatcher;
import org.lwd.frame.ctrl.context.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author lwd
 */
@Component("frame.carousel.local-service")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LocalServiceImpl implements LocalService {
    @Inject
    private HeaderAware headerAware;
    @Inject
    private RequestAware requestAware;
    @Inject
    private SessionAware sessionAware;
    @Inject
    private ResponseAware responseAware;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private Response response;
    private String uri;
    private String ip;
    private String sessionId;
    private Map<String, String> header;
    private Map<String, String> parameter;

    @Override
    public LocalService build(String uri, String ip, String sessionId, Map<String, String> header, Map<String, String> parameter) {
        this.uri = uri;
        this.ip = ip;
        this.sessionId = sessionId;
        this.header = header;
        this.parameter = parameter;

        return this;
    }

    @Override
    public String call() throws Exception {
        headerAware.set(new LocalHeaderAdapter(ip, header));
        requestAware.set(new LocalRequestAdapter(uri, parameter));
        sessionAware.set(new LocalSessionAdapter(sessionId));
        responseAware.set(new LocalResponseAdapter());
        dispatcher.execute();
        response.getOutputStream().flush();
        response.getOutputStream().close();

        return response.getOutputStream().toString();
    }
}
