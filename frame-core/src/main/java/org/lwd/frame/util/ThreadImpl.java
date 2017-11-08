package org.lwd.frame.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lwd
 */
@Component("frame.util.thread")
public class ThreadImpl implements Thread {
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;

    @Override
    public void sleep(int time, TimeUnit unit) {
        sleep(time * unit.getTime());
    }

    @Override
    public void sleep(int min, int max, TimeUnit unit) {
        sleep(generator.random(min * unit.getTime(), max * unit.getTime()));
    }

    private void sleep(long time) {
        try {
            java.lang.Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.warn(e, "线程休眠[{}]时发生异常！", time);
        }
    }
}
