package org.lwd.frame.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 日志切片。主要实现isDebugEnable、isInfoEnable的覆盖率测试。
 *
 * @author lwd
 */
@Aspect
@Component("frame.test.aspect.logger")
public class LoggerAspect {
    private AtomicInteger debug = new AtomicInteger();
    private AtomicInteger info = new AtomicInteger();

    @Around("target(org.lwd.frame.util.Logger)")
    public Object enable(ProceedingJoinPoint point) throws Throwable {
        String name = point.getSignature().getName();
        if (name.equals("isDebugEnable"))
            return debug.incrementAndGet() % 2 == 0;

        if (name.equals("isInfoEnable"))
            return info.incrementAndGet() % 2 == 0;

        return point.proceed();
    }
}
