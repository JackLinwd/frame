package org.lwd.frame.ctrl;

import org.lwd.frame.scheduler.MinuteJob;
import org.lwd.frame.util.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lwd
 */
@Controller("frame.ctrl.handler")
public class HandlerImpl implements Handler, MinuteJob {
    @Value("${frame.ctrl.handler.queue:true}")
    private boolean queue;
    @Value("${frame.ctrl.handler.max-idle:30}")
    private int maxIdle;
    private Map<String, ExecutorService> queueService = new ConcurrentHashMap<>();
    private Map<String, Long> queueTime = new ConcurrentHashMap<>();

    @Override
    public <T> T call(String key, Callable<T> callable) throws Exception {
        if (!queue)
            return callable.call();

        T t = getExecutorService(key).submit(callable).get();
        queueTime.put(key, System.currentTimeMillis());

        return t;
    }

    @Override
    public void run(String key, Runnable runnable) {
        if (queue) {
            getExecutorService(key).submit(runnable);
            queueTime.put(key, System.currentTimeMillis());
        } else
            runnable.run();
    }

    private ExecutorService getExecutorService(String key) {
        return queueService.computeIfAbsent(key, k -> Executors.newSingleThreadExecutor());
    }

    @Override
    public void clear(String key) {
        if (queueService.containsKey(key))
            queueService.remove(key).shutdown();
        if (queueTime.containsKey(key))
            queueTime.remove(key);
    }

    @Override
    public void executeMinuteJob() {
        if (!queue)
            return;

        Set<String> set = new HashSet<>();
        queueTime.forEach((key, time) -> {
            if (System.currentTimeMillis() - time > maxIdle * TimeUnit.Minute.getTime())
                set.add(key);
        });
        set.forEach(this::clear);
    }
}
