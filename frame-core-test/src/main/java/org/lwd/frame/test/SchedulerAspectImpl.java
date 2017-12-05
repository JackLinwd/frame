package org.lwd.frame.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author lwd
 */
@Aspect
@Component("frame.test.aspect.scheduler")
public class SchedulerAspectImpl implements SchedulerAspect {
    protected boolean paused;

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void press() {
        paused = false;
    }

    @Around("target(org.lwd.frame.scheduler.Scheduler)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        return paused ? null : point.proceed();
    }
}
