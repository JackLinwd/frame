package org.lwd.frame.carousel;

import com.alibaba.fastjson.JSONObject;
import org.lwd.frame.bean.BeanFactory;
import org.lwd.frame.bean.ContextClosedListener;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.cache.Cache;
import org.lwd.frame.crypto.Sign;
import org.lwd.frame.ctrl.context.Header;
import org.lwd.frame.ctrl.context.Session;
import org.lwd.frame.ctrl.execute.Execute;
import org.lwd.frame.ctrl.execute.ExecuteListener;
import org.lwd.frame.ctrl.execute.Executor;
import org.lwd.frame.ctrl.status.Status;
import org.lwd.frame.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author lwd
 */
@Component("frame.carousel.helper")
public class CarouselHelperImpl implements CarouselHelper, ExecuteListener, ContextRefreshedListener, ContextClosedListener {
    private static final String CACHE_SERVICE = "frame.carousel.helper.service:";

    @Inject
    private Sign sign;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Inject
    private Cache cache;
    @Inject
    private Json json;
    @Inject
    private Status status;
    @Inject
    private Header header;
    @Inject
    private Session session;
    @Inject
    private Optional<Set<CarouselRegister>> registers;
    @Value("${frame.carousel.url:}")
    private String carouselUrl;
    @Value("${frame.ctrl.service-root:}")
    private String serviceUrl;
    @Value("${frame.carousel.service.all:false}")
    private boolean serviceAll;
    private boolean emptyCarouselUrl;
    private boolean emptyServiceUrl;
    private Map<String, String> services = new ConcurrentHashMap<>();
    private ExecutorService executorService;

    @Override
    public boolean config(String name, String description, ActionBuilder actionBuilder) {
        return config(name, description, actionBuilder, 0, 0, 0, false);
    }

    @Override
    public boolean config(String name, String description, ActionBuilder actionBuilder, int delay, int interval, int times, boolean wait) {
        if (emptyCarouselUrl || validator.isEmpty(name) || actionBuilder == null || validator.isEmpty(carouselUrl))
            return false;

        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("description", description);
        object.put("actions", actionBuilder.get());
        if (delay > 0)
            object.put("delay", delay);
        if (interval > 0)
            object.put("interval", interval);
        if (times > 0)
            object.put("times", times);
        if (wait)
            object.put("wait", 1);

        return code0(http.post(carouselUrl + "/config/update", null, object.toString()));
    }

    @Override
    public String process(String name, int delay, Map<String, String> header, String data) {
        if (emptyCarouselUrl)
            return null;

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        if (delay > 0)
            map.put("delay", "" + delay);
        map.put("data", data);

        return http.post(carouselUrl + "/process/execute", header, map);
    }

    @Override
    public boolean register(String key, String service) {
        services.put(key, service);
        if (emptyCarouselUrl || emptyServiceUrl)
            return false;

        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        map.put("service", serviceUrl + service);
        map.put("validate", serviceUrl + status.getUri());
        map.put("success", "^\\{\"code\":0,.*");

        return code0(http.post(carouselUrl + "/discovery/register", null, map));
    }

    @Override
    public String service(String key, Map<String, String> header, Map<String, String> parameter) {
        return service(key, header, parameter, 0);
    }

    @Override
    public String service(String key, Map<String, String> header, Map<String, String> parameter, int cacheTime) {
        if (logger.isDebugEnable())
            logger.debug("开始获取Carousel服务[key={};header={};parameter={};cacheTime={}]。", key, header, parameter, cacheTime);

        if (services.containsKey(key)) {
            try {
                if (logger.isDebugEnable())
                    logger.debug("使用本地微服务[{}:{}]。", key, services.get(key));
                Future<String> future = executorService.submit(BeanFactory.getBean(LocalService.class)
                        .build(services.get(key), null, session.getId(), header, parameter));

                return future.get();
            } catch (Throwable e) {
                logger.warn(e, "执行本地服务[{}:{}]时发生异常！", key, services.get(key));

                return null;
            }
        }

        if (emptyCarouselUrl)
            return null;

        String cacheKey = null;
        boolean cacheable = cacheTime > 0;
        if (cacheable) {
            cacheKey = CACHE_SERVICE + key + converter.toString(header) + converter.toString(parameter)
                    + (System.currentTimeMillis() / cacheTime / TimeUnit.Minute.getTime());
            String string = cache.get(cacheKey);
            if (logger.isDebugEnable())
                logger.debug("使用缓存[{}]的Carousel数据[{}]。", cacheKey, string);
            if (string != null)
                return string;
        }

        if (header == null)
            header = new HashMap<>();
        header.put("carousel-ds-key", key);

        return cacheService(cacheable, cacheKey, http.post(carouselUrl + "/discovery/execute", header, parameter));
    }

    private String cacheService(boolean cacheable, String cacheKey, String result) {
        if (logger.isDebugEnable())
            logger.debug("缓存[{}:{}]Carousel请求数据[{}]。", cacheable, cacheKey, result);
        JSONObject json = this.json.toObject(result);
        if (json != null && json.containsKey("data"))
            result = json.getString("data");
        if (cacheable)
            cache.put(cacheKey, result, false);

        return result;
    }

    private boolean code0(String string) {
        if (validator.isEmpty(string))
            return false;

        JSONObject object = json.toObject(string);

        return object != null && object.containsKey("code") && object.getIntValue("code") == 0;
    }

    @Override
    public void sign(String serviceKey, String signKey, Map<String, String> parameter) {
        if (services.containsKey(serviceKey))
            return;

        sign.put(parameter, signKey);
    }

    @Override
    public void definition(Execute classExecute, Execute methodExecute, Executor executor) {
        if (!serviceAll)
            return;

        for (String name : converter.toArray(methodExecute.name(), ",")) {
            if (classExecute == null)
                services.put(name, name);
            else
                services.put(classExecute.key() + "." + name, classExecute.name() + name);
        }
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        emptyCarouselUrl = validator.isEmpty(carouselUrl);
        emptyServiceUrl = validator.isEmpty(serviceUrl);
        registers.ifPresent(set -> set.forEach(register -> services.putAll(register.getKeyService())));
        services.forEach(this::register);
        if (executorService == null)
            executorService = Executors.newCachedThreadPool();

        if (!services.isEmpty() && logger.isInfoEnable())
            logger.info("本地发现服务[{}:{}]发布完成。", services.size(), converter.toString(services));
    }

    @Override
    public int getContextClosedSort() {
        return 9;
    }

    @Override
    public void onContextClosed() {
        if (executorService != null)
            executorService.shutdown();
    }
}
