package org.lwd.frame.ctrl;

import org.lwd.frame.scheduler.MinuteJob;
import org.lwd.frame.util.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author lwd
 */
@Controller("frame.ctrl.handler")
public class HandlerImpl implements Handler, MinuteJob {
    @Inject
    private Counter counter;
    @Value("${frame.ctrl.handler.queue:true}")
    private boolean queue;
    @Value("${frame.ctrl.handler.max-idle:30}")
    private int maxIdle;
    private Map<Integer, ExecutorService> executors = new ConcurrentHashMap<>();
    private Map<Integer, List<Future<?>>> futures = new ConcurrentHashMap<>();
    private Map<Integer, Long> times = new ConcurrentHashMap<>();

    @Override
    public <T> T call(String key, Callable<T> callable) throws Exception {
        return queue ? submit(key, callable).get() : callable.call();
    }

    @Override
    public <T> Future<T> submit(String key, Callable<T> callable) {
        Integer nKey = getKey(key);
        Future<T> future = getExecutorService(nKey).submit(callable);
        addFuture(nKey, future);

        return future;
    }

    @Override
    public void run(String key, Runnable runnable) {
        if (queue) {
            Integer nKey = getKey(key);
            addFuture(nKey, getExecutorService(nKey).submit(runnable));
        } else
            runnable.run();
    }

    private ExecutorService getExecutorService(Integer key) {
        return executors.computeIfAbsent(key, k -> Executors.newSingleThreadExecutor());
    }

    private void addFuture(Integer key, Future<?> future) {
        futures.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(future);
    }

    @Override
    public void clear(String key) {
        clear(getKey(key));
    }

    private void clear(Integer key) {
        if (executors.containsKey(key))
            executors.remove(key).shutdown();
        if (futures.containsKey(key))
            futures.remove(key);
        if (times.containsKey(key))
            times.remove(key);
    }

    private Integer getKey(String key) {
        return key == null ? 0 : (key.hashCode() & Integer.MAX_VALUE) % counter.max();
    }

    @Override
    public void executeMinuteJob() {
        if (!queue)
            return;

        Set<Integer> set = new HashSet<>();
        futures.forEach((key, list) -> {
            if (list.isEmpty()) {
                if (System.currentTimeMillis() - times.getOrDefault(key, 0L) > maxIdle * TimeUnit.Minute.getTime())
                    set.add(key);

                return;
            }

            Set<Future<?>> dones = new HashSet<>();
            list.stream().filter(Future::isDone).forEach(dones::add);
            if (dones.isEmpty())
                return;

            list.removeAll(dones);
            if (list.isEmpty())
                times.put(key, System.currentTimeMillis());
        });
        if (!set.isEmpty())
            set.forEach(this::clear);
    }
}
