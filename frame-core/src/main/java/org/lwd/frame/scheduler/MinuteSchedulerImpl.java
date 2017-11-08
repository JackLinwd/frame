package org.lwd.frame.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * @author lwd
 */
@Component("frame.scheduler.minute")
public class MinuteSchedulerImpl extends SchedulerSupport<MinuteJob> implements MinuteScheduler {
    @Inject
    private Optional<Set<MinuteJob>> jobs;

    @Scheduled(cron = "${frame.scheduler.minute.cron:0 * * * * ?}")
    @Override
    public synchronized void execute() {
        jobs.ifPresent(set -> {
            if (logger.isDebugEnable())
                logger.debug("开始执行每分钟定时器调度。。。");

            set.forEach(this::pool);

            if (logger.isDebugEnable())
                logger.debug("成功执行{}个每分钟定时器任务！", set.size());
        });
    }

    @Override
    protected void execute(MinuteJob job) {
        job.executeMinuteJob();
    }
}
