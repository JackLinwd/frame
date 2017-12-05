package org.lwd.frame.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lwd
 */
@Aspect
@Component("frame.test.aspect.generator")
public class GeneratorAspectImpl implements GeneratorAspect {
    private List<String> strings;
    private List<Integer> ints;

    @Override
    public void randomString(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public void randomInt(List<Integer> ints) {
        this.ints = ints;
    }

    @Around("target(org.lwd.frame.util.Generator)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String name = point.getSignature().getName();
        if (name.equals("random")) {
            if (point.getArgs().length == 1 && strings != null && !strings.isEmpty())
                return strings.remove(0);

            if (point.getArgs().length == 2 && ints != null && !ints.isEmpty())
                return ints.remove(0);
        }

        return point.proceed();
    }
}
