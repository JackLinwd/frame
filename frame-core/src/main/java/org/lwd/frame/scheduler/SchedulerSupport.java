package org.lwd.frame.scheduler;

import io.netty.util.internal.ConcurrentSet;
import org.lwd.frame.atomic.Closables;
import org.lwd.frame.atomic.Failable;
import org.lwd.frame.bean.ContextClosedListener;
import org.lwd.frame.bean.ContextRefreshedListener;
import org.lwd.frame.util.Logger;
import org.lwd.frame.util.Validator;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时器支持类。
 *
 * @author lwd
 */
public abstract class SchedulerSupport<T> implements ContextRefreshedListener, ContextClosedListener {
    @Inject
    protected Validator validator;
    @Inject
    protected Logger logger;
    @Inject
    protected Optional<Set<Failable>> failables;
    @Inject
    protected Closables closables;
    protected Set<Integer> runningJobs;
    protected ExecutorService executorService;

    /**
     * 添加到执行线程池中。
     *
     * @param job 要执行的任务实例。
     */
    protected void pool(T job) {
        if (runningJobs == null)
            return;

        if (isRunning(job))
            return;

        executorService.submit(() -> {
            begin(job);

            try {
                execute(job);
            } catch (Throwable e) {
                exception(e);
            } finally {
                finish(job);
            }
        });
    }

    /**
     * 判断指定Job是否正在运行。
     *
     * @param job 要验证的Job对象。
     * @return 如果正在运行则返回true；否则返回false。
     */
    protected boolean isRunning(T job) {
        return job != null && runningJobs.contains(job.hashCode());
    }

    /**
     * 将Job设置为正在运行。
     *
     * @param job 要设置的Job对象。
     */
    protected void begin(T job) {
        if (job != null)
            runningJobs.add(job.hashCode());
    }

    /**
     * 执行任务。
     *
     * @param job 要执行的任务实例。
     */
    protected abstract void execute(T job);

    /**
     * 处理执行异常。
     *
     * @param throwable 异常信息。
     */
    protected void exception(Throwable throwable) {
        failables.ifPresent(set -> set.forEach(failable -> failable.fail(throwable)));

        logger.warn(throwable, "执行定时器任务时发生异常！");
    }

    /**
     * 设置Job对象运行结束。
     *
     * @param job 要设置的Job对象。
     */
    protected void finish(T job) {
        if (job != null)
            runningJobs.remove(job.hashCode());
        closables.close();
    }

    @Override
    public int getContextRefreshedSort() {
        return 99;
    }

    @Override
    public void onContextRefreshed() {
        runningJobs = new ConcurrentSet<>();
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public int getContextClosedSort() {
        return 99;
    }

    @Override
    public void onContextClosed() {
        executorService.shutdownNow();
    }
}
