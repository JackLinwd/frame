package org.lwd.frame.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * @author lwd
 */
@Component("frame.scheduler.hour")
public class HourSchedulerImpl extends SchedulerSupport<HourJob> implements HourScheduler {
    @Inject
    private Optional<Set<HourJob>> jobs;

    @Scheduled(cron = "${frame.scheduler.hour.cron:30 0 * * * ?}")
    @Override
    public synchronized void execute() {
        jobs.ifPresent(set -> {
            if (logger.isDebugEnable())
                logger.debug("开始执行每小时定时器调度。。。");

            set.forEach(this::pool);

            if (logger.isDebugEnable())
                logger.debug("成功执行{}个每小时定时器任务！", set.size());
        });
    }

    @Override
    protected void execute(HourJob job) {
        job.executeHourJob();
    }
}
