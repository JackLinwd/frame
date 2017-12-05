package org.lwd.frame.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author lwd
 */
@Aspect
@Component("frame.test.aspect.date-time")
public class DateTimeAspectImpl implements DateTimeAspect {
    private List<Timestamp> nows;

    @Override
    public void now(List<Timestamp> nows) {
        this.nows = nows;
    }

    @Around("target(org.lwd.frame.util.DateTime)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String name = point.getSignature().getName();
        if (name.equals("now") && nows != null && nows.size() > 0)
            return nows.remove(0);

        return point.proceed();
    }
}
